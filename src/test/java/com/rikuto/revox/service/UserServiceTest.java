package com.rikuto.revox.service;

import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.dto.user.UserUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.UserResponseMapper;
import com.rikuto.revox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserResponseMapper userResponseMapper;

	@InjectMocks
	private UserService userService;

	private User testUser;
	private UserUpdateRequest updateRequest;
	private UserResponse userResponse;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.id(1)
				.uniqueUserId("test-unique-id")
				.nickname("テストユーザー")
				.displayEmail("test@example.com")
				.isDeleted(false)
				.build();

		updateRequest = UserUpdateRequest.builder()
				.nickname("更新後ニックネーム")
				.build();

		userResponse = UserResponse.builder()
				.id(1)
				.nickname("更新後ニックネーム")
				.displayEmail("test@example.com")
				.build();
	}

	@Nested
	class FindByIdAndUpdateUserTests {
		@Test
		void ユーザーIDが存在する場合ユーザーを返す() {
			Integer userId = 1;
			when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(testUser));

			User result = userService.findById(userId);

			assertThat(result).isEqualTo(testUser);
			verify(userRepository).findByIdAndIsDeletedFalse(userId);
		}

		@Test
		void ユーザーが存在しない場合ResourceNotFoundExceptionをスロー() {
			Integer userId = 999;
			when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> userService.findById(userId))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessage("ユーザーが見つかりません");
			verify(userRepository).findByIdAndIsDeletedFalse(userId);
		}

		@Test
		void 正常にユーザー情報を更新() {
			Integer userId = 1;
			User updatedUser = User.builder()
					.id(1)
					.uniqueUserId("test-unique-id")
					.nickname("更新後ニックネーム")
					.displayEmail("test@example.com")
					.isDeleted(false)
					.build();

			when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(testUser));
			when(userRepository.save(testUser)).thenReturn(updatedUser);
			when(userResponseMapper.toResponse(updatedUser)).thenReturn(userResponse);

			UserResponse result = userService.updateUser(updateRequest, userId);

			assertThat(result).isEqualTo(userResponse);
			verify(userRepository).findByIdAndIsDeletedFalse(userId);
			verify(userRepository).save(testUser);
			verify(userResponseMapper).toResponse(updatedUser);
		}
	}

	@Nested
	class SoftDeleteUserTests {
		@Test
		void 正常にユーザーを論理削除() {
			Integer userId = 1;
			when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(testUser));

			userService.softDeleteUser(userId);

			verify(userRepository).findByIdAndIsDeletedFalse(userId);
			verify(userRepository).save(testUser);
			assertThat(testUser.isDeleted()).isTrue();
		}

		@Test
		void 削除対象ユーザーが見つからない場合ResourceNotFoundExceptionをスロー() {
			Integer userId = 999;
			when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> userService.softDeleteUser(userId))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessage("ユーザーが見つかりません");

			verify(userRepository).findByIdAndIsDeletedFalse(userId);
			verify(userRepository, never()).save(any());
		}
	}

	@Nested
	class FindOrCreateUserTests {
		private String uniqueUserId;
		private String name;
		private String email;

		@BeforeEach
		void setup() {
			uniqueUserId = "existing-unique-id";
			name = "既存ユーザー";
			email = "existing@example.com";
		}

		@Test
		void 論理削除されていない既存ユーザーが存在する場合そのユーザーを返す() {
			when(userRepository.findByUniqueUserId(uniqueUserId)).thenReturn(Optional.of(testUser));

			User result = userService.findOrCreateUser(uniqueUserId, name, email);

			assertThat(result).isEqualTo(testUser);
			verify(userRepository).findByUniqueUserId(uniqueUserId);
			verify(userRepository, never()).save(any());
		}

		@Test
		void 論理削除されたユーザーが存在する場合復元して返す() {
			User deletedUser = User.builder()
					.id(2)
					.uniqueUserId(uniqueUserId)
					.nickname("削除済みユーザー")
					.displayEmail("deleted@example.com")
					.isDeleted(true)
					.build();

			when(userRepository.findByUniqueUserId(uniqueUserId)).thenReturn(Optional.of(deletedUser));
			when(userRepository.save(any(User.class))).thenReturn(deletedUser);

			User result = userService.findOrCreateUser(uniqueUserId, name, email);

			assertThat(result).isEqualTo(deletedUser);
			assertThat(result.isDeleted()).isFalse();
			verify(userRepository).findByUniqueUserId(uniqueUserId);
			verify(userRepository).save(deletedUser);
		}

		@Test
		void 新規ユーザーの場合新しいユーザーを作成して返す() {
			String newUniqueUserId = "new-unique-id";
			String newName = "新規ユーザー";
			String newEmail = "new@example.com";

			when(userRepository.findByUniqueUserId(newUniqueUserId)).thenReturn(Optional.empty());
			when(userRepository.save(any(User.class))).thenReturn(
					User.builder()
							.id(3)
							.uniqueUserId(newUniqueUserId)
							.nickname(newName)
							.displayEmail(newEmail)
							.isDeleted(false)
							.build()
			);

			User result = userService.findOrCreateUser(newUniqueUserId, newName, newEmail);

			assertThat(result).isNotNull();
			assertThat(result.getUniqueUserId()).isEqualTo(newUniqueUserId);
			assertThat(result.getNickname()).isEqualTo(newName);
			assertThat(result.getDisplayEmail()).isEqualTo(newEmail);

			verify(userRepository).findByUniqueUserId(newUniqueUserId);
			verify(userRepository).save(any(User.class));
		}
	}
}