package com.rikuto.revox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikuto.revox.dto.aiquestion.AiQuestionCreateRequest;
import com.rikuto.revox.dto.aiquestion.AiQuestionResponse;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.service.AiQuestionService;
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
		controllers = AiQuestionController.class,
		excludeAutoConfiguration = {
				SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class
		}
)
@Import(AiQuestionControllerTest.AiQuestionServiceTestConfig.class)
class AiQuestionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AiQuestionService aiQuestionService;

	private AiQuestionCreateRequest commonAiQuestionCreateRequest;
	private AiQuestionResponse commonAiQuestionResponse;

	private final Integer testUserId = 1;
	private final Integer testBikeId = 101;
	private final Integer testCategoryId = 1;
	private final Integer testAiQuestionId = 201;

	@BeforeEach
	void setUp() {
		commonAiQuestionCreateRequest = AiQuestionCreateRequest.builder()
				.userId(testUserId)
				.bikeId(testBikeId)
				.categoryId(testCategoryId)
				.question("エンジンオイルの交換時期はいつですか？")
				.build();

		commonAiQuestionResponse = AiQuestionResponse.builder()
				.id(testAiQuestionId)
				.userId(testUserId)
				.bikeId(testBikeId)
				.categoryId(testCategoryId)
				.question("エンジンオイルの交換時期はいつですか？")
				.answer("エンジンオイルは3,000km～5,000kmまたは6ヶ月ごとに交換することをお勧めします。")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		reset(aiQuestionService);
	}

	@Test
	void AI質問が正常に作成され201を返すこと() throws Exception {

		when(aiQuestionService.createAiQuestion(any())).thenReturn(commonAiQuestionResponse);

		mockMvc.perform(post("/api/ai-questions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonAiQuestionCreateRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(testAiQuestionId))
				.andExpect(jsonPath("$.question").value("エンジンオイルの交換時期はいつですか？"))
				.andExpect(jsonPath("$.answer").value("エンジンオイルは3,000km～5,000kmまたは6ヶ月ごとに交換することをお勧めします。"));


		verify(aiQuestionService).createAiQuestion(any());
	}

	@Test
	void バリデーションエラー時は400を返すこと() throws Exception {

		AiQuestionCreateRequest invalidRequest = AiQuestionCreateRequest.builder()
				.bikeId(testBikeId)
				.categoryId(testCategoryId)
				.question("エンジンオイルの交換時期はいつですか？")
				.build();

		mockMvc.perform(post("/api/ai-questions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());

		verify(aiQuestionService, never()).createAiQuestion(any());
	}

	@Test
	void 質問内容が空文字の場合400を返すこと() throws Exception {

		AiQuestionCreateRequest invalidRequest = AiQuestionCreateRequest.builder()
				.userId(testUserId)
				.bikeId(testBikeId)
				.categoryId(testCategoryId)
				.question("")
				.build();

		mockMvc.perform(post("/api/ai-questions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());

		verify(aiQuestionService, never()).createAiQuestion(any());
	}

	@Test
	void ユーザーIDに紐づくAI質問履歴を正常に取得できること() throws Exception {

		when(aiQuestionService.getAiQuestionByUserId(testUserId)).thenReturn(List.of(commonAiQuestionResponse));

		mockMvc.perform(get("/api/ai-questions/user/{userId}", testUserId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(testAiQuestionId))
				.andExpect(jsonPath("$[0].question").value("エンジンオイルの交換時期はいつですか？"));

		verify(aiQuestionService).getAiQuestionByUserId(testUserId);
	}

	@Test
	void 存在しないユーザーのAI質問履歴取得時に404を返すこと() throws Exception {

		when(aiQuestionService.getAiQuestionByUserId(testUserId))
				.thenThrow(new ResourceNotFoundException("ユーザーが見つかりません"));

		mockMvc.perform(get("/api/ai-questions/user/{userId}", testUserId))
				.andExpect(status().isNotFound());

		verify(aiQuestionService).getAiQuestionByUserId(testUserId);
	}

	@Test
	void AI質問作成時にユーザーが見つからない場合404を返すこと() throws Exception {

		when(aiQuestionService.createAiQuestion(any()))
				.thenThrow(new ResourceNotFoundException("ユーザーが見つかりません"));

		mockMvc.perform(post("/api/ai-questions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commonAiQuestionCreateRequest)))
				.andExpect(status().isNotFound());

		verify(aiQuestionService).createAiQuestion(any());
	}


	@TestConfiguration
	static class AiQuestionServiceTestConfig {
		@Bean
		public AiQuestionService aiQuestionService() {
			return Mockito.mock(AiQuestionService.class);
		}
	}
}