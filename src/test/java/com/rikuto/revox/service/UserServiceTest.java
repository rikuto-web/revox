package com.rikuto.revox.service;

import com.rikuto.revox.domain.User;
import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.dto.user.UserUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.UserResponseMapper;
import com.rikuto.revox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

	@Test
	void 正常にユーザーを論理削除() {
		Integer userId = 1;
		when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(testUser));

		userService.softDeleteUser(userId);

		verify(userRepository).findByIdAndIsDeletedFalse(userId);
		verify(userRepository).save(testUser);
	}

	@Test
	void 既存ユーザーが存在する場合既存ユーザーを返す() {
		String uniqueUserId = "existing-unique-id";
		String name = "既存ユーザー";
		String email = "existing@example.com";

		when(userRepository.findByUniqueUserIdAndIsDeletedFalse(uniqueUserId))
				.thenReturn(Optional.of(testUser));

		User result = userService.findOrCreateUser(uniqueUserId, name, email);

		assertThat(result).isEqualTo(testUser);
		verify(userRepository).findByUniqueUserIdAndIsDeletedFalse(uniqueUserId);
		verify(userRepository, never()).save(any());
	}

	@Test
	void  新規ユーザーの場合新しいユーザーを作成して返す() {
		String uniqueUserId = "new-unique-id";
		String name = "新規ユーザー";
		String email = "new@example.com";

		User savedUser = User.builder()
				.uniqueUserId(uniqueUserId)
				.nickname(name)
				.displayEmail(email)
				.build();

		when(userRepository.findByUniqueUserIdAndIsDeletedFalse(uniqueUserId))
				.thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		User result = userService.findOrCreateUser(uniqueUserId, name, email);

		assertThat(result).isEqualTo(savedUser);
		verify(userRepository).findByUniqueUserIdAndIsDeletedFalse(uniqueUserId);
		verify(userRepository).save(argThat(user ->
				user.getUniqueUserId().equals(uniqueUserId) &&
						user.getNickname().equals(name) &&
						user.getDisplayEmail().equals(email)
		));
	}
}