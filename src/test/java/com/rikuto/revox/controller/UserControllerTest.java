package com.rikuto.revox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.dto.user.UserUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	private MockMvc mockMvc;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(userController)
				.build();
		objectMapper = new ObjectMapper();
	}

	@Test
	void 正常にユーザーニックネームを更新し200を返す() throws Exception {
		Integer userId = 1;
		UserUpdateRequest request = UserUpdateRequest.builder().nickname("更新後ニックネーム").build();
		UserResponse response = UserResponse.builder().id(userId).nickname("更新後ニックネーム").build();

		when(userService.updateUser(any(UserUpdateRequest.class), eq(userId))).thenReturn(response);

		mockMvc.perform(put("/api/users/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(userId))
				.andExpect(jsonPath("$.nickname").value("更新後ニックネーム"));

		verify(userService).updateUser(any(UserUpdateRequest.class), eq(userId));
	}


	@Test
	void ユーザーが存在しない場合404を返す() throws Exception {
		Integer userId = 999;
		UserUpdateRequest request = UserUpdateRequest.builder().nickname("更新後ニックネーム").build();

		when(userService.updateUser(any(UserUpdateRequest.class), eq(userId)))
				.thenThrow(new ResourceNotFoundException("ユーザーが見つかりません"));

		mockMvc.perform(put("/api/users/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isNotFound());

		verify(userService).updateUser(any(UserUpdateRequest.class), eq(userId));
	}

	@Test
	void 正常にユーザーを論理削除し204を返す() throws Exception {
		Integer userId = 1;
		doNothing().when(userService).softDeleteUser(userId);

		mockMvc.perform(patch("/api/users/{userId}/delete", userId))
				.andExpect(status().isNoContent());

		verify(userService).softDeleteUser(userId);
	}

	@Test
	void 削除対象ユーザーが存在しない場合404を返す() throws Exception {
		Integer userId = 999;
		doThrow(new ResourceNotFoundException("ユーザーが見つかりません"))
				.when(userService).softDeleteUser(userId);

		mockMvc.perform(patch("/api/users/{userId}/delete", userId))
				.andExpect(status().isNotFound());

		verify(userService).softDeleteUser(userId);
	}
}