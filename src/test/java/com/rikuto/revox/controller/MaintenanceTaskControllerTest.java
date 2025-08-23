package com.rikuto.revox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.service.MaintenanceTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
		controllers = MaintenanceTaskController.class,
		excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class MaintenanceTaskControllerTest {

	private final Integer testBikeId = 101;
	private final Integer testCategoryId = 1;
	private final Integer testMaintenanceTaskId = 301;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MaintenanceTaskService maintenanceTaskService;

	private MaintenanceTaskRequest commonMaintenanceTaskRequest;
	private MaintenanceTaskUpdateRequest commonMaintenanceTaskUpdateRequest;
	private MaintenanceTaskResponse commonMaintenanceTaskResponse;
	private List<MaintenanceTaskResponse> commonMaintenanceTaskResponseList;

	@BeforeEach
	void setUp() {
		commonMaintenanceTaskRequest = MaintenanceTaskRequest.builder()
				.categoryId(testCategoryId)
				.bikeId(testBikeId)
				.name("オイル交換手順")
				.description("1. エンジンを温める\n2. ドレンボルトを外す\n3. 新しいオイルを注入する")
				.build();

		commonMaintenanceTaskUpdateRequest = MaintenanceTaskUpdateRequest.builder()
				.name("新しいオイル交換手順")
				.description("更新された説明")
				.build();

		commonMaintenanceTaskResponse = MaintenanceTaskResponse.builder()
				.id(testMaintenanceTaskId)
				.categoryId(testCategoryId)
				.name("オイル交換手順")
				.description("1. エンジンを温める\n2. ドレンボルトを外す\n3. 新しいオイルを注入する")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		commonMaintenanceTaskResponseList = List.of(commonMaintenanceTaskResponse);
	}

	@Nested
	class PostTests {
		@Test
		void 新しい整備タスクが正常に登録され201を返すこと() throws Exception {
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
					.bikeId(testBikeId)
					.name("") // バリデーションエラー
					.description("説明")
					.build();

			mockMvc.perform(post("/api/maintenance-task")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(invalidRequest)))
					.andExpect(status().isBadRequest());

			verify(maintenanceTaskService, never()).registerMaintenanceTask(any());
		}

		@Test
		void 紐づくリソースが見つからない場合は404を返すこと() throws Exception {
			when(maintenanceTaskService.registerMaintenanceTask(any()))
					.thenThrow(new ResourceNotFoundException("リソースが見つかりません"));

			mockMvc.perform(post("/api/maintenance-task")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(commonMaintenanceTaskRequest)))
					.andExpect(status().isNotFound());

			verify(maintenanceTaskService).registerMaintenanceTask(any());
		}
	}

	@Nested
	class GetTests {
		@Test
		void ユーザーIDに紐づくタスクを正常に取得し200を返すこと() throws Exception {
			Integer testUserId = 1;
			when(maintenanceTaskService.findLatestMaintenanceTasksByUserId(testUserId))
					.thenReturn(commonMaintenanceTaskResponseList);

			mockMvc.perform(get("/api/maintenance-task/user/{userId}", testUserId)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$[0].id").value(testMaintenanceTaskId))
					.andExpect(jsonPath("$[0].name").value("オイル交換手順"));

			verify(maintenanceTaskService).findLatestMaintenanceTasksByUserId(testUserId);
		}

		@Test
		void バイクIDに紐づくタスクを正常に取得し200を返すこと() throws Exception {
			when(maintenanceTaskService.findByBikeId(testBikeId))
					.thenReturn(commonMaintenanceTaskResponseList);

			mockMvc.perform(get("/api/maintenance-task/bike/{bikeId}", testBikeId)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$[0].id").value(testMaintenanceTaskId));

			verify(maintenanceTaskService).findByBikeId(testBikeId);
		}

		@Test
		void バイクIDとカテゴリIDに紐づくタスクを正常に取得し200を返すこと() throws Exception {
			when(maintenanceTaskService.findByBikeIdAndCategoryId(testBikeId, testCategoryId))
					.thenReturn(commonMaintenanceTaskResponseList);

			mockMvc.perform(get("/api/maintenance-task/bike/{bikeId}/category/{categoryId}", testBikeId, testCategoryId)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$[0].id").value(testMaintenanceTaskId));

			verify(maintenanceTaskService).findByBikeIdAndCategoryId(testBikeId, testCategoryId);
		}
	}

	@Nested
	class UpdateTests {
		@Test
		void 既存の整備タスクが正常に更新され200を返すこと() throws Exception {
			when(maintenanceTaskService.updateMaintenanceTask(eq(testMaintenanceTaskId), any(MaintenanceTaskUpdateRequest.class)))
					.thenReturn(commonMaintenanceTaskResponse);

			mockMvc.perform(patch("/api/maintenance-task/{maintenanceTaskId}", testMaintenanceTaskId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(commonMaintenanceTaskUpdateRequest)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(testMaintenanceTaskId))
					.andExpect(jsonPath("$.name").value("オイル交換手順"));

			verify(maintenanceTaskService).updateMaintenanceTask(eq(testMaintenanceTaskId), any(MaintenanceTaskUpdateRequest.class));
		}

		@Test
		void 更新時に整備タスクが見つからない場合は404を返すこと() throws Exception {
			when(maintenanceTaskService.updateMaintenanceTask(eq(testMaintenanceTaskId), any(MaintenanceTaskUpdateRequest.class)))
					.thenThrow(new ResourceNotFoundException("整備タスクが見つかりません"));

			mockMvc.perform(patch("/api/maintenance-task/{maintenanceTaskId}", testMaintenanceTaskId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(commonMaintenanceTaskUpdateRequest)))
					.andExpect(status().isNotFound());

			verify(maintenanceTaskService).updateMaintenanceTask(eq(testMaintenanceTaskId), any(MaintenanceTaskUpdateRequest.class));
		}
	}

	@Nested
	class SoftDeleteTests {
		@Test
		void 論理削除が成功し204を返すこと() throws Exception {
			doNothing().when(maintenanceTaskService).softDeleteMaintenanceTask(testMaintenanceTaskId);

			mockMvc.perform(patch("/api/maintenance-task/{maintenanceTaskId}/softDelete", testMaintenanceTaskId))
					.andExpect(status().isNoContent());

			verify(maintenanceTaskService).softDeleteMaintenanceTask(testMaintenanceTaskId);
		}

		@Test
		void 存在しない整備タスクの削除時に404を返すこと() throws Exception {
			doThrow(new ResourceNotFoundException("整備タスクが見つかりません"))
					.when(maintenanceTaskService).softDeleteMaintenanceTask(testMaintenanceTaskId);

			mockMvc.perform(patch("/api/maintenance-task/{maintenanceTaskId}/softDelete", testMaintenanceTaskId))
					.andExpect(status().isNotFound());

			verify(maintenanceTaskService).softDeleteMaintenanceTask(testMaintenanceTaskId);
		}
	}
}