package com.rikuto.revox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.service.BikeService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
		controllers = BikeController.class,
		excludeAutoConfiguration = {
				SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class
		}
)

@Import(BikeControllerTest.BikeServiceTestConfig.class)
class BikeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BikeService bikeService;

	private BikeCreateRequest commonBikeCreateRequest;
	private BikeResponse commonBikeResponse;

	private final Integer testBikeId = 101;
	private final Integer testUserId = 1;

	@BeforeEach
	void setUp() {
		commonBikeCreateRequest = BikeCreateRequest.builder()
				.manufacturer("Honda")
				.modelName("CBR250RR")
				.modelCode("MC51")
				.modelYear(2023)
				.currentMileage(1000)
				.purchaseDate(LocalDate.of(2023, 1, 1))
				.imageUrl("http://example.com/cbr.jpg")
				.userId(testUserId)
				.build();

		commonBikeResponse = BikeResponse.builder()
				.id(testBikeId)
				.manufacturer("Honda")
				.modelName("CBR250RR")
				.modelCode("MC51")
				.modelYear(2023)
				.currentMileage(1000)
				.purchaseDate(LocalDate.of(2023, 1, 1))
				.imageUrl("http://example.com/cbr.jpg")
				.userId(testUserId)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		reset(bikeService);
	}

	@Test
	void ユーザーIDに紐づくバイク一覧を正常に取得できること() throws Exception {

		when(bikeService.findBikeByUserId(testUserId)).thenReturn(List.of(commonBikeResponse));

		mockMvc.perform(get("/api/bikes/user/{userId}", testUserId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(testBikeId))
				.andExpect(jsonPath("$[0].modelName").value("CBR250RR"));

		verify(bikeService).findBikeByUserId(testUserId);
	}

	@Test
	void ユーザーIDに紐づくバイクが存在しない場合404を返すこと() throws Exception {

		when(bikeService.findBikeByUserId(testUserId))
				.thenThrow(new ResourceNotFoundException("見つかりません"));

		mockMvc.perform(get("/api/bikes/user/{userId}", testUserId))
				.andExpect(status().isNotFound());

		verify(bikeService).findBikeByUserId(testUserId);
	}

	@Test
	void 新しいバイク情報が正常に登録され201を返すこと() throws Exception {

		when(bikeService.registerBike(any())).thenReturn(commonBikeResponse);

		mockMvc.perform(post("/api/bikes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonBikeCreateRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(testBikeId))
				.andExpect(jsonPath("$.modelName").value("CBR250RR"));

		verify(bikeService).registerBike(any());
	}

	@Test
	void バリデーションエラー時は400を返すこと() throws Exception {

		BikeCreateRequest invalidRequest = BikeCreateRequest.builder()
				.modelName("CBR250RR")
				.modelCode("MC51")
				.modelYear(2023)
				.currentMileage(1000)
				.purchaseDate(LocalDate.of(2023, 1, 1))
				.imageUrl("http://example.com/cbr.jpg")
				.userId(testUserId)
				.build();

		mockMvc.perform(post("/api/bikes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());

		verify(bikeService, never()).registerBike(any());
	}

	@Test
	void バイク情報が正常に更新されること() throws Exception {

		when(bikeService.updateBike(any(), eq(testBikeId))).thenReturn(commonBikeResponse);

		mockMvc.perform(put("/api/bikes/{bikeId}", testBikeId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonBikeCreateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(testBikeId))
				.andExpect(jsonPath("$.modelName").value("CBR250RR"));

		verify(bikeService).updateBike(any(), eq(testBikeId));
	}

	@Test
	void バイクが見つからない場合は更新時に404を返す() throws Exception {

		when(bikeService.updateBike(any(), eq(testBikeId)))
				.thenThrow(new ResourceNotFoundException("バイクが見つかりません"));

		mockMvc.perform(put("/api/bikes/{bikeId}", testBikeId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonBikeCreateRequest)))
				.andExpect(status().isNotFound());

		verify(bikeService).updateBike(any(), eq(testBikeId));
	}

	@Test
	void 論理削除が成功し204を返すこと() throws Exception {

		doNothing().when(bikeService).softDeleteBike(testUserId, testBikeId);

		mockMvc.perform(delete("/api/bikes/{userId}/{bikeId}", testUserId, testBikeId))
				.andExpect(status().isNoContent());

		verify(bikeService).softDeleteBike(testUserId, testBikeId);
	}


	@TestConfiguration
	static class BikeServiceTestConfig {
		@Bean
		public BikeService bikeService() {
			return Mockito.mock(BikeService.class);
		}
	}
}
