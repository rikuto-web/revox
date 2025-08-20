package com.rikuto.revox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikuto.revox.dto.ai.AiQuestionCreateRequest;
import com.rikuto.revox.dto.ai.AiQuestionResponse;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.service.AiService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
		controllers = AiController.class,
		excludeAutoConfiguration = {
				SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class
		}
)
@Import(AiControllerTest.AiQuestionServiceTestConfig.class)
class AiControllerTest {

	//正常系
	private final Integer testUserId = 1;
	private final Integer testBikeId = 2;
	private final Integer testCategoryId = 3;
	private final Integer testAiId = 4;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AiService aiService;
	private AiQuestionCreateRequest commonAiQuestionCreateRequest;
	private AiQuestionResponse commonAiQuestionResponse;

	@BeforeEach
	void setUp() {
		commonAiQuestionCreateRequest = AiQuestionCreateRequest.builder()
				.question("エンジンオイルの交換時期はいつですか？")
				.build();

		commonAiQuestionResponse = AiQuestionResponse.builder()
				.id(testAiId)
				.userId(testUserId)
				.bikeId(testBikeId)
				.categoryId(testCategoryId)
				.question("エンジンオイルの交換時期はいつですか？")
				.answer("エンジンオイルは3,000km～5,000kmまたは6ヶ月ごとに交換することをお勧めします。")
				.createdAt(LocalDateTime.now())
				.build();

		reset(aiService);
	}

	@TestConfiguration
	static class AiQuestionServiceTestConfig {
		@Bean
		public AiService aiQuestionService() {
			return Mockito.mock(AiService.class);
		}
	}

	@Nested
	class CreateAiTests {
		@Test
		void AIからの回答が正常に作成され200を返すこと() throws Exception {
			when(aiService.createAiQuestion(
					any(AiQuestionCreateRequest.class),
					eq(testUserId),
					eq(testBikeId),
					eq(testCategoryId)
			)).thenReturn(commonAiQuestionResponse);

			mockMvc.perform(post("/api/ai/user/{userId}/bike/{bikeId}/category/{categoryId}",
							testUserId, testBikeId, testCategoryId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(commonAiQuestionCreateRequest)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(testAiId))
					.andExpect(jsonPath("$.question").value("エンジンオイルの交換時期はいつですか？"))
					.andExpect(jsonPath("$.answer").value("エンジンオイルは3,000km～5,000kmまたは6ヶ月ごとに交換することをお勧めします。"));

			verify(aiService).createAiQuestion(any(), eq(testUserId), eq(testBikeId), eq(testCategoryId));
		}

		@Test
		void バリデーションエラー時は400BadRequestを返すこと() throws Exception {
			AiQuestionCreateRequest invalidRequest = AiQuestionCreateRequest.builder()
					.question("")
					.build();

			mockMvc.perform(post("/api/ai/user/{userId}/bike/{bikeId}/category/{categoryId}",
							testUserId, testBikeId, testCategoryId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(invalidRequest)))
					.andExpect(status().isBadRequest());

			verify(aiService, never()).createAiQuestion(any(), any(), any(), any());
		}
	}

	@Nested
	class GetAiHistoryTests {
		@Test
		void ユーザーIDに紐づくAI質問履歴を正常に取得できること() throws Exception {
			when(aiService.getAiQuestionByUserId(testUserId)).thenReturn(List.of(commonAiQuestionResponse));

			mockMvc.perform(get("/api/ai/user/{userId}", testUserId)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$[0].id").value(testAiId))
					.andExpect(jsonPath("$[0].question").value("エンジンオイルの交換時期はいつですか？"));

			verify(aiService).getAiQuestionByUserId(testUserId);
		}

		@Test
		void 存在しないユーザーのAI質問履歴取得時に404を返すこと() throws Exception {
			when(aiService.getAiQuestionByUserId(anyInt()))
					.thenThrow(new ResourceNotFoundException("ユーザーが見つかりません"));

			mockMvc.perform(get("/api/ai/user/{userId}", testUserId))
					.andExpect(status().isNotFound());

			verify(aiService).getAiQuestionByUserId(testUserId);
		}
	}
}