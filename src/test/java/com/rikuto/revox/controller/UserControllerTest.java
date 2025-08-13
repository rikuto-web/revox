package com.rikuto.revox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.dto.user.UserUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
		controllers = UserController.class,
		excludeAutoConfiguration = {
				SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class
		}
)
@Import(UserControllerTest.UserServiceTestConfig.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	private UserUpdateRequest commonUserUpdateRequest;
	private UserResponse commonUserResponse;

	private final Integer testUserId = 1;
	private final Integer notFoundUserId = 999;
	private final String updatedNickname = "更新後ニックネーム";

	@BeforeEach
	void setUp() {
		commonUserUpdateRequest = UserUpdateRequest.builder()
				.nickname(updatedNickname)
				.build();

		commonUserResponse = UserResponse.builder()
				.id(testUserId)
				.nickname(updatedNickname)
				.build();

		reset(userService);
	}

	@Nested
	class UpdateUserTests {
		@Test
		void 正常にユーザーニックネームを更新し200を返すこと() throws Exception {
			when(userService.updateUser(any(UserUpdateRequest.class), eq(testUserId))).thenReturn(commonUserResponse);

			mockMvc.perform(patch("/api/users/{userId}", testUserId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(commonUserUpdateRequest)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(testUserId))
					.andExpect(jsonPath("$.nickname").value(updatedNickname));

			verify(userService).updateUser(any(UserUpdateRequest.class), eq(testUserId));
		}

		@Test
		void ユーザーが存在しない場合404を返すこと() throws Exception {
			when(userService.updateUser(any(UserUpdateRequest.class), eq(notFoundUserId)))
					.thenThrow(new ResourceNotFoundException("ユーザーが見つかりません"));

			mockMvc.perform(patch("/api/users/{userId}", notFoundUserId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(commonUserUpdateRequest)))
					.andExpect(status().isNotFound());

			verify(userService).updateUser(any(UserUpdateRequest.class), eq(notFoundUserId));
		}
	}

	@Nested
	class SoftDeleteUserTests {
		@Test
		void 正常にユーザーを論理削除し204を返すこと() throws Exception {
			doNothing().when(userService).softDeleteUser(testUserId);

			mockMvc.perform(patch("/api/users/{userId}/softDelete", testUserId))
					.andExpect(status().isNoContent());

			verify(userService).softDeleteUser(testUserId);
		}

		@Test
		void 削除対象ユーザーが存在しない場合404を返すこと() throws Exception {
			doThrow(new ResourceNotFoundException("ユーザーが見つかりません"))
					.when(userService).softDeleteUser(notFoundUserId);

			mockMvc.perform(patch("/api/users/{userId}/softDelete", notFoundUserId))
					.andExpect(status().isNotFound());

			verify(userService).softDeleteUser(notFoundUserId);
		}
	}

	/**
	 * UserServiceのモックBeanを定義するテスト用の設定クラス。
	 * このクラスを`@Import`することで、テストコンテキストにモックが提供されます。
	 */
	@TestConfiguration
	static class UserServiceTestConfig {
		@Bean
		public UserService userService() {
			return Mockito.mock(UserService.class);
		}
	}
}