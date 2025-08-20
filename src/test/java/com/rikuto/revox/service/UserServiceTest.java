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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
				.id(testUser.getId())
				.uniqueUserId(testUser.getUniqueUserId())
				.nickname("更新後ニックネーム")
				.displayEmail("test@example.com")
				.build();
	}

	private void stubUserFound() {
		when(userRepository.findByIdAndIsDeletedFalse(testUser.getId())).thenReturn(Optional.of(testUser));
	}

	@Nested
	class CreateTests {
		@Test
		void 新規ユーザーの場合新しいユーザーを作成して返す() {
			String newUniqueUserId = "new-unique-id";
			String newName = "新規ユーザー";
			String newEmail = "new@example.com";

			when(userRepository.findByUniqueUserId(newUniqueUserId)).thenReturn(Optional.empty());
			when(userRepository.save(any(User.class))).thenReturn(
					User.builder()
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

	@Nested
	class ReadTests {
		@Test
		void 存在するユーザーIDで適切にユーザー情報を返すこと() {
			stubUserFound();

			User result = userService.findById(testUser.getId());

			assertThat(result).isEqualTo(testUser);
			verify(userRepository).findByIdAndIsDeletedFalse(testUser.getId());
		}

		@Test
		void 論理削除されていない既存ユーザーが存在する場合そのユーザーを返す() {
			String uniqueUserId = "existing-unique-id";
			when(userRepository.findByUniqueUserId(uniqueUserId)).thenReturn(Optional.of(testUser));

			User result = userService.findOrCreateUser(uniqueUserId, "既存ユーザー", "existing@example.com");

			assertThat(result).isEqualTo(testUser);
			verify(userRepository).findByUniqueUserId(uniqueUserId);
			verify(userRepository, never()).save(any());
		}

		@Test
		void 論理削除されたユーザーが存在する場合復元して返す() {
			String uniqueUserId = "existing-unique-id";
			User deletedUser = User.builder()
					.uniqueUserId(uniqueUserId)
					.nickname("削除済みユーザー")
					.displayEmail("deleted@example.com")
					.isDeleted(true)
					.build();

			when(userRepository.findByUniqueUserId(uniqueUserId)).thenReturn(Optional.of(deletedUser));
			when(userRepository.save(any(User.class))).thenReturn(deletedUser);

			User result = userService.findOrCreateUser(uniqueUserId, "既存ユーザー", "existing@example.com");

			assertThat(result).isEqualTo(deletedUser);
			assertThat(result.isDeleted()).isFalse();
			verify(userRepository).findByUniqueUserId(uniqueUserId);
			verify(userRepository).save(deletedUser);
		}

		@Test
		void ユーザーが存在しない場合ResourceNotFoundExceptionをスロー() {
			Integer dummyUserId = 999;
			when(userRepository.findByIdAndIsDeletedFalse(dummyUserId)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> userService.findById(dummyUserId))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessage("ユーザーが見つかりません" + dummyUserId);
			verify(userRepository).findByIdAndIsDeletedFalse(dummyUserId);
		}
	}

	@Nested
	class UpdateTests {
		@Test
		void 既存のユーザー情報が正常に更新されユーザー情報を返すこと() {
			stubUserFound();

			User updatedUser = User.builder()
					.id(testUser.getId())
					.uniqueUserId(testUser.getUniqueUserId())
					.nickname(updateRequest.getNickname())
					.displayEmail(testUser.getDisplayEmail())
					.isDeleted(false)
					.build();

			when(userRepository.save(any(User.class))).thenReturn(updatedUser);
			when(userResponseMapper.toResponse(any(User.class))).thenReturn(userResponse);

			UserResponse result = userService.updateUser(updateRequest, testUser.getId());

			assertThat(result).isEqualTo(userResponse);
			verify(userRepository).findByIdAndIsDeletedFalse(testUser.getId());
			verify(userRepository).save(any(User.class));
			verify(userResponseMapper).toResponse(any(User.class));
		}

		@Test
		void 更新対象ユーザーが存在しない場合ResourceNotFoundExceptionをスロー() {
			Integer dummyUserId = 999;
			when(userRepository.findByIdAndIsDeletedFalse(dummyUserId)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> userService.updateUser(updateRequest, dummyUserId))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessage("ユーザーが見つかりません" + dummyUserId);

			verify(userRepository, never()).save(any());
		}
	}

	@Nested
	class DeleteTests {
		@Test
		void 既存のユーザー情報が正常に論理削除されること() {
			stubUserFound();

			userService.softDeleteUser(testUser.getId());

			verify(userRepository).findByIdAndIsDeletedFalse(testUser.getId());
			verify(userRepository).save(testUser);
			assertThat(testUser.isDeleted()).isTrue();
		}

		@Test
		void 削除対象ユーザーが見つからない場合ResourceNotFoundExceptionをスロー() {
			Integer dummyUserId = 999;
			when(userRepository.findByIdAndIsDeletedFalse(dummyUserId)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> userService.softDeleteUser(dummyUserId))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessage("ユーザーが見つかりません" + dummyUserId);

			verify(userRepository).findByIdAndIsDeletedFalse(dummyUserId);
			verify(userRepository, never()).save(any());
		}
	}
}