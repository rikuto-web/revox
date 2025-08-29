package com.rikuto.revox.service;

import com.rikuto.revox.domain.Ai;
import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.Bike;
import com.rikuto.revox.domain.User;
import com.rikuto.revox.dto.ai.AiCreatePrompt;
import com.rikuto.revox.dto.ai.AiQuestionCreateRequest;
import com.rikuto.revox.dto.ai.AiQuestionResponse;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.AiMapper;
import com.rikuto.revox.repository.AiRepository;
import com.rikuto.revox.repository.BikeRepository;
import com.rikuto.revox.repository.CategoryRepository;
import com.rikuto.revox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BikeRepository bikeRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private AiRepository aiRepository;

	@Mock
	private AiMapper aiMapper;

	@Mock
	private GeminiService geminiService;

	@InjectMocks
	private AiService aiService;

	private User testUser;
	private Bike testBike;
	private Category testCategory;
	private Ai testAi;
	private AiQuestionCreateRequest commonAiQuestionCreateRequest;
	private AiQuestionResponse commonAiQuestionResponse;

	@BeforeEach
	void setUp() {
		testUser = User.builder().id(1).nickname("testUser").build();
		testBike = Bike.builder().id(2).user(testUser).manufacturer("TestBike").modelName("Test").build();
		testCategory = Category.builder().id(3).name("エンジン").displayOrder(1).build();
		testAi = Ai.builder().user(testUser).bike(testBike).category(testCategory).question("エンジンオイルの交換時期はいつですか？").answer("エンジンオイルは3,000km～5,000kmまたは6ヶ月ごとに交換することをお勧めします。").build();

		commonAiQuestionCreateRequest = AiQuestionCreateRequest.builder().question("エンジンオイルの交換時期はいつですか？").build();

		commonAiQuestionResponse = AiQuestionResponse.builder().id(testAi.getId()).userId(testUser.getId()).bikeId(testBike.getId()).categoryId(testCategory.getId()).question("エンジンオイルの交換時期はいつですか？").answer("エンジンオイルは3,000km～5,000kmまたは6ヶ月ごとに交換することをお勧めします。").build();
	}

	private void stubUserFound() {
		when(userRepository.findByIdAndIsDeletedFalse(testUser.getId())).thenReturn(Optional.of(testUser));
	}

	private void stubUserNotFound() {
		when(userRepository.findByIdAndIsDeletedFalse(testUser.getId())).thenReturn(Optional.empty());
	}

	private void stubBikeFound() {
		when(bikeRepository.findByIdAndUserIdAndIsDeletedFalse(testBike.getId(), testUser.getId())).thenReturn(Optional.of(testBike));
	}

	private void stubBikeNotFound() {
		when(bikeRepository.findByIdAndUserIdAndIsDeletedFalse(testBike.getId(), testUser.getId())).thenReturn(Optional.empty());
	}

	private void stubCategoryFound() {
		when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
	}

	private void stubCategoryNotFound() {
		when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.empty());
	}

	@Nested
	class CreateAiTests {

		@Test
		void AI質問が正常に作成され作成されたAI質問情報が返されること() {
			stubUserFound();
			stubBikeFound();
			stubCategoryFound();

			when(geminiService.generateContent(any(AiCreatePrompt.class))).thenReturn("MockedAIAnswer");

			when(aiMapper.toResponse(testAi)).thenReturn(commonAiQuestionResponse);

			when(aiRepository.save(any(Ai.class))).thenReturn(testAi);

			AiQuestionResponse result = aiService.createAiQuestion(commonAiQuestionCreateRequest, testUser.getId(), testBike.getId(), testCategory.getId());

			assertThat(result).isEqualTo(commonAiQuestionResponse);

			verify(userRepository).findByIdAndIsDeletedFalse(testUser.getId());
			verify(bikeRepository).findByIdAndUserIdAndIsDeletedFalse(testBike.getId(), testUser.getId());
			verify(categoryRepository).findById(testCategory.getId());
			verify(aiRepository).save(any(Ai.class));
			verify(aiMapper).toResponse(testAi);
			verify(geminiService, times(1)).generateContent(any(AiCreatePrompt.class));
		}

		@Test
		void ユーザーが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubUserNotFound();

			assertThatThrownBy(() -> aiService.createAiQuestion(commonAiQuestionCreateRequest, testUser.getId(), testBike.getId(), testCategory.getId())).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("ユーザーID " + testUser.getId() + " が見つかりません。");

			verify(bikeRepository, never()).findByIdAndUserIdAndIsDeletedFalse(any(), any());
			verify(categoryRepository, never()).findById(any());
			verify(aiRepository, never()).save(any());
		}

		@Test
		void バイクが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubUserFound();
			stubBikeNotFound();

			assertThatThrownBy(() -> aiService.createAiQuestion(commonAiQuestionCreateRequest, testUser.getId(), testBike.getId(), testCategory.getId())).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("ユーザー ID " + testUser.getId() + " に紐づくバイクID " + testBike.getId() + "が見つかりません。");

			verify(categoryRepository, never()).findById(any());
			verify(aiRepository, never()).save(any());
		}

		@Test
		void カテゴリーが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubUserFound();
			stubBikeFound();
			stubCategoryNotFound();

			assertThatThrownBy(() -> aiService.createAiQuestion(commonAiQuestionCreateRequest, testUser.getId(), testBike.getId(), testCategory.getId())).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("カテゴリーID " + testCategory.getId() + " が見つかりません。");

			verify(aiRepository, never()).save(any());
		}
	}

	@Nested
	class GetAiByUserIdTests {

		@Test
		void ユーザーIDに紐づくAI質問履歴を正しく取得できること() {
			List<Ai> questionList = List.of(testAi);

			when(aiRepository.findByUserId(testUser.getId())).thenReturn(questionList);
			when(aiMapper.toResponse(testAi)).thenReturn(commonAiQuestionResponse);

			List<AiQuestionResponse> result = aiService.getAiQuestionByUserId(testUser.getId());

			assertThat(result).hasSize(1);
			assertThat(result.getFirst()).isEqualTo(commonAiQuestionResponse);

			verify(aiRepository).findByUserId(testUser.getId());
			verify(aiMapper).toResponse(testAi);
		}

		@Test
		void ユーザーにAI質問履歴がない場合は空のリストを返すこと() {
			when(aiRepository.findByUserId(testUser.getId())).thenReturn(List.of());

			List<AiQuestionResponse> result = aiService.getAiQuestionByUserId(testUser.getId());

			assertThat(result).isEmpty();

			verify(aiRepository).findByUserId(testUser.getId());
		}
	}
}