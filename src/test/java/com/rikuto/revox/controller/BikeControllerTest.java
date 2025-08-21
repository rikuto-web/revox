package com.rikuto.revox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.service.BikeService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
		controllers = BikeController.class,
		excludeAutoConfiguration = {
				SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class
		}
)
@Import(BikeControllerTest.BikeServiceTestConfig.class)
class BikeControllerTest {

	private final Integer testUserId = 1;
	private final Integer testBikeId = 2;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private BikeService bikeService;
	private BikeCreateRequest commonBikeCreateRequest;
	private BikeResponse commonBikeResponse;

	@BeforeEach
	void setUp() {
		commonBikeCreateRequest = BikeCreateRequest.builder()
				.manufacturer("Test")
				.modelName("TestBike")
				.modelCode("test")
				.modelYear(2023)
				.currentMileage(1000)
				.purchaseDate(LocalDate.of(2023, 1, 1))
				.imageUrl("http://example.com/cbr.jpg")
				.build();

		commonBikeResponse = BikeResponse.builder()
				.userId(testUserId)
				.id(testBikeId)
				.manufacturer("Test")
				.modelName("TestBike")
				.modelCode("test")
				.modelYear(2023)
				.currentMileage(1000)
				.purchaseDate(LocalDate.of(2023, 1, 1))
				.imageUrl("http://example.com/cbr.jpg")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		reset(bikeService);
	}

	/**
	 * BikeServiceのモックBeanを定義するテスト用の設定クラス
	 */
	@TestConfiguration
	static class BikeServiceTestConfig {
		@Bean
		public BikeService bikeService() {
			return Mockito.mock(BikeService.class);
		}
	}

	@Nested
	class GetTests {
		@Test
		void ユーザーIDに紐づくバイク一覧を正常に取得できること() throws Exception {
			BikeResponse secondBike = BikeResponse.builder()
					.manufacturer("TestBike")
					.modelName("SecondTestBike")
					.build();

			when(bikeService.findBikeByUserId(testUserId)).thenReturn(List.of(commonBikeResponse, secondBike));

			mockMvc.perform(get("/api/bikes/user/{userId}", testUserId)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$[0].id").value(testBikeId))
					.andExpect(jsonPath("$[0].modelName").value("TestBike"))
					.andExpect(jsonPath("$[1].modelName").value("SecondTestBike"));

			verify(bikeService).findBikeByUserId(testUserId);
		}

		@Test
		void 指定されたユーザーIDとバイクIDで単一のバイク情報を取得できること() throws Exception {
			when(bikeService.findByIdAndUserId(testBikeId, testUserId)).thenReturn(commonBikeResponse);

			mockMvc.perform(get("/api/bikes/user/{userId}/bike/{bikeId}", testUserId, testBikeId)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id").value(testBikeId))
					.andExpect(jsonPath("$.modelName").value("TestBike"));

			verify(bikeService).findByIdAndUserId(testBikeId, testUserId);
		}

		@Test
		void 指定されたユーザーIDに紐づくバイクが存在しない場合404を返すこと() throws Exception {
			when(bikeService.findBikeByUserId(testUserId))
					.thenThrow(new ResourceNotFoundException("ユーザーが見つかりません"));

			mockMvc.perform(get("/api/bikes/user/{userId}", testUserId))
					.andExpect(status().isNotFound());

			verify(bikeService).findBikeByUserId(testUserId);
		}
	}

	@Nested
	class PostTests {
		@Test
		void 新しいバイク情報が正常に登録され201を返すこと() throws Exception {
			when(bikeService.registerBike(any(), any())).thenReturn(commonBikeResponse);

			mockMvc.perform(post("/api/bikes/user/{userId}", testUserId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(commonBikeCreateRequest)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(testBikeId))
					.andExpect(jsonPath("$.modelName").value("TestBike"));

			verify(bikeService).registerBike(any(), any());
		}

		@Test
		void バリデーションエラー時は400を返すこと() throws Exception {
			BikeCreateRequest invalidRequest = BikeCreateRequest.builder()
					.modelName("TestBike")
					.modelCode("Test")
					.build();

			mockMvc.perform(post("/api/bikes/user/{userId}", testUserId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(invalidRequest)))
					.andExpect(status().isBadRequest());

			verify(bikeService, never()).registerBike(any(), any());
		}
	}

	@Nested
	class UpdateTests {
		@Test
		void バイク情報が正常に更新されること() throws Exception {
			when(bikeService.updateBike(any(), eq(testBikeId), eq(testUserId))).thenReturn(commonBikeResponse);

			mockMvc.perform(patch("/api/bikes/user/{userId}/bike/{bikeId}", testUserId, testBikeId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(commonBikeCreateRequest)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(testBikeId))
					.andExpect(jsonPath("$.modelName").value("TestBike"));

			verify(bikeService).updateBike(any(), eq(testBikeId), eq(testUserId));
		}

		@Test
		void バイクが見つからない場合は更新時に404を返す() throws Exception {
			when(bikeService.updateBike(any(), eq(testBikeId), eq(testUserId)))
					.thenThrow(new ResourceNotFoundException("バイクが見つかりません"));

			mockMvc.perform(patch("/api/bikes/user/{userId}/bike/{bikeId}", testUserId, testBikeId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(commonBikeCreateRequest)))
					.andExpect(status().isNotFound());

			verify(bikeService).updateBike(any(), eq(testBikeId), eq(testUserId));
		}
	}

	@Nested
	class SoftDeleteTests {
		@Test
		void 論理削除が成功し204を返すこと() throws Exception {
			doNothing().when(bikeService).softDeleteBike(testBikeId, testUserId);

			mockMvc.perform(patch("/api/bikes/user/{userId}/bike/{bikeId}/softDelete", testUserId, testBikeId))
					.andExpect(status().isNoContent());

			verify(bikeService).softDeleteBike(testBikeId, testUserId);
		}
	}
}