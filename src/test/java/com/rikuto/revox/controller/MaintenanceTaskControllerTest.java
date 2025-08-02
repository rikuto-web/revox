package com.rikuto.revox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.service.MaintenanceTaskService;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
		controllers = MaintenanceTaskController.class,
		excludeAutoConfiguration = {
				SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class
		}
)
@Import(MaintenanceTaskControllerTest.MaintenanceTaskServiceTestConfig.class)
class MaintenanceTaskControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MaintenanceTaskService maintenanceTaskService;

	private MaintenanceTaskRequest commonMaintenanceTaskRequest;
	private MaintenanceTaskResponse commonMaintenanceTaskResponse;

	private final Integer testCategoryId = 1;
	private final Integer testMaintenanceTaskId = 301;

	@BeforeEach
	void setUp() {
		commonMaintenanceTaskRequest = MaintenanceTaskRequest.builder()
				.categoryId(testCategoryId)
				.name("オイル交換手順")
				.description("1. エンジンを温める\n2. ドレンボルトを外す\n3. 新しいオイルを注入する")
				.build();

		commonMaintenanceTaskResponse = MaintenanceTaskResponse.builder()
				.id(testMaintenanceTaskId)
				.categoryId(testCategoryId)
				.name("オイル交換手順")
				.description("1. エンジンを温める\n2. ドレンボルトを外す\n3. 新しいオイルを注入する")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		reset(maintenanceTaskService);
	}

	@Test
	void カテゴリーIDに紐づく整備タスクを正常に取得できること() throws Exception {

		when(maintenanceTaskService.findMaintenanceTaskByCategoryId(testCategoryId))
				.thenReturn(List.of(commonMaintenanceTaskResponse));

		mockMvc.perform(get("/api/maintenance-task/category/{categoryId}", testCategoryId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(testMaintenanceTaskId))
				.andExpect(jsonPath("$[0].name").value("オイル交換手順"));

		verify(maintenanceTaskService).findMaintenanceTaskByCategoryId(testCategoryId);
	}

	@Test
	void 整備タスクが正常に登録され201を返すこと() throws Exception {

		when(maintenanceTaskService.registerMaintenanceTask(any())).thenReturn(commonMaintenanceTaskResponse);

		mockMvc.perform(post("/api/maintenance-task")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonMaintenanceTaskRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(testMaintenanceTaskId))
				.andExpect(jsonPath("$.name").value("オイル交換手順"));

		verify(maintenanceTaskService).registerMaintenanceTask(any());
	}

	@Test
	void バリデーションエラー時は400を返すこと() throws Exception {

		MaintenanceTaskRequest invalidRequest = MaintenanceTaskRequest.builder()
				.categoryId(testCategoryId)
				.description("説明のみで名前が未設定")
				.build(); // nameが未設定

		mockMvc.perform(post("/api/maintenance-task")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());

		verify(maintenanceTaskService, never()).registerMaintenanceTask(any());
	}

	@Test
	void 整備タスクが正常に更新されること() throws Exception {

		when(maintenanceTaskService.updateMaintenanceTask(eq(testMaintenanceTaskId), any()))
				.thenReturn(commonMaintenanceTaskResponse);

		mockMvc.perform(put("/api/maintenance-task/{maintenanceTaskId}", testMaintenanceTaskId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonMaintenanceTaskRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(testMaintenanceTaskId))
				.andExpect(jsonPath("$.name").value("オイル交換手順"));

		verify(maintenanceTaskService).updateMaintenanceTask(eq(testMaintenanceTaskId), any());
	}

	@Test
	void 整備タスクが見つからない場合は更新時に404を返すこと() throws Exception {

		when(maintenanceTaskService.updateMaintenanceTask(eq(testMaintenanceTaskId), any()))
				.thenThrow(new ResourceNotFoundException("整備タスクが見つかりません"));

		mockMvc.perform(put("/api/maintenance-task/{maintenanceTaskId}", testMaintenanceTaskId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonMaintenanceTaskRequest)))
				.andExpect(status().isNotFound());

		verify(maintenanceTaskService).updateMaintenanceTask(eq(testMaintenanceTaskId), any());
	}

	@Test
	void 論理削除が成功し204を返すこと() throws Exception {

		doNothing().when(maintenanceTaskService).softDeleteMaintenanceTask(testMaintenanceTaskId);

		mockMvc.perform(delete("/api/maintenance-task/{maintenanceTaskId}", testMaintenanceTaskId))
				.andExpect(status().isNoContent());

		verify(maintenanceTaskService).softDeleteMaintenanceTask(testMaintenanceTaskId);
	}

	@Test
	void 存在しない整備タスクの削除時に404を返すこと() throws Exception {

		doThrow(new ResourceNotFoundException("整備タスクが見つかりません"))
				.when(maintenanceTaskService).softDeleteMaintenanceTask(testMaintenanceTaskId);

		mockMvc.perform(delete("/api/maintenance-task/{maintenanceTaskId}", testMaintenanceTaskId))
				.andExpect(status().isNotFound());

		verify(maintenanceTaskService).softDeleteMaintenanceTask(testMaintenanceTaskId);
	}

	@Test
	void カテゴリーが見つからない場合は登録時に404を返すこと() throws Exception {

		when(maintenanceTaskService.registerMaintenanceTask(any()))
				.thenThrow(new ResourceNotFoundException("カテゴリーが見つかりません"));

		mockMvc.perform(post("/api/maintenance-task")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonMaintenanceTaskRequest)))
				.andExpect(status().isNotFound());

		verify(maintenanceTaskService).registerMaintenanceTask(any());
	}


	@TestConfiguration
	static class MaintenanceTaskServiceTestConfig {
		@Bean
		public MaintenanceTaskService maintenanceTaskService() {
			return Mockito.mock(MaintenanceTaskService.class);
		}
	}
}