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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BikeControllerのテストクラスです。
 * コントローラー層のresponseが正しく動作するか検証しています。
 * SpringSecurity機能はテスト時は無効にしています。
 */
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
		//リクエストおよびレスポンスの準備
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
	void ユーザーIDに紐づくバイク情報が正常に取得されること() throws Exception {
		//Given
		when(bikeService.findBikeByUserId(testUserId)).thenReturn(commonBikeResponse);
		//When&Then
		mockMvc.perform(get("/api/bikes/user/{userId}", testUserId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(commonBikeResponse.getId()))
				.andExpect(jsonPath("$.modelName").value(commonBikeResponse.getModelName()))
				.andExpect(jsonPath("$.userId").value(commonBikeResponse.getUserId()));

		verify(bikeService, times(1)).findBikeByUserId(testUserId);
	}

	@Test
	void ユーザーIDに紐づくバイク情報が存在しない場合に404NotFoundを返すこと() throws Exception {
		//Given
		when(bikeService.findBikeByUserId(testUserId))
				.thenThrow(new ResourceNotFoundException("ユーザーID " + testUserId + " に紐づくバイクが見つかりません。"));
		//When&Then
		mockMvc.perform(get("/api/bikes/user/{userId}", testUserId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(bikeService, times(1)).findBikeByUserId(testUserId);
	}

	@Test
	void 新しいバイク情報が正常に登録され201Createdを返すこと() throws Exception {
		//Given
		when(bikeService.registerBike(any(BikeCreateRequest.class))).thenReturn(commonBikeResponse);
		//When&Then
		mockMvc.perform(post("/api/bikes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonBikeCreateRequest)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(commonBikeResponse.getId()))
				.andExpect(jsonPath("$.modelName").value(commonBikeResponse.getModelName()));

		verify(bikeService, times(1)).registerBike(any(BikeCreateRequest.class));
	}

	@Test
	void 新しいバイク情報登録時にバリデーションエラーがある場合に400BadRequestを返すこと() throws Exception {
		//Given
		BikeCreateRequest invalidRequest = BikeCreateRequest.builder()
				.manufacturer(null) // バリデーションエラー
				.modelName("CBR250RR")
				.modelCode("MC51")
				.modelYear(2023)
				.currentMileage(1000)
				.purchaseDate(LocalDate.of(2023, 1, 1))
				.imageUrl("http://example.com/cbr.jpg")
				.userId(testUserId)
				.build();
		//When&Then
		mockMvc.perform(post("/api/bikes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());

		verify(bikeService, never()).registerBike(any(BikeCreateRequest.class));
	}

	@Test
	void 既存のバイク情報が正常に更新され200OKを返すこと() throws Exception {
		//Given
		when(bikeService.updateBike(eq(testBikeId), any(BikeCreateRequest.class))).thenReturn(commonBikeResponse);
		//When&Then
		mockMvc.perform(put("/api/bikes/{bikeId}", testBikeId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonBikeCreateRequest)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(commonBikeResponse.getId()))
				.andExpect(jsonPath("$.modelName").value(commonBikeResponse.getModelName()));

		verify(bikeService, times(1)).updateBike(eq(testBikeId), any(BikeCreateRequest.class));
	}

	@Test
	void 既存のバイク情報更新時にバイクが見つからない場合に404NotFoundを返すこと() throws Exception {
		//Given
		when(bikeService.updateBike(eq(testBikeId), any(BikeCreateRequest.class)))
				.thenThrow(new ResourceNotFoundException("バイクID " + testBikeId + " が見つかりません。"));
		//When&Then
		mockMvc.perform(put("/api/bikes/{bikeId}", testBikeId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonBikeCreateRequest)))
				.andExpect(status().isNotFound());

		verify(bikeService, times(1)).updateBike(eq(testBikeId), any(BikeCreateRequest.class));
	}

	@Test
	void バイク情報論理削除が正常に完了し204NoContentを返すこと() throws Exception {
		//Given
		doNothing().when(bikeService).softDeleteBike(testBikeId);
		//When&Then
		mockMvc.perform(delete("/api/bikes/{bikeId}", testBikeId))
				.andExpect(status().isNoContent());

		verify(bikeService, times(1)).softDeleteBike(testBikeId);
	}

	// Mock用設定クラス
	@TestConfiguration
	static class BikeServiceTestConfig {
		@Bean
		public BikeService bikeService() {
			return Mockito.mock(BikeService.class);
		}
	}
}
