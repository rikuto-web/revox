package com.rikuto.revox.service;

import com.rikuto.revox.domain.AiQuestion;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.dto.aiquestion.AiQuestionCreateRequest;
import com.rikuto.revox.dto.aiquestion.AiQuestionPrompt;
import com.rikuto.revox.dto.aiquestion.AiQuestionResponse;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.AiQuestionMapper;
import com.rikuto.revox.repository.AiQuestionRepository;
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
import org.springframework.boot.test.mock.mockito.MockBean;

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
class AiQuestionServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BikeRepository bikeRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private AiQuestionRepository aiQuestionRepository;

	@Mock
	private AiQuestionMapper aiQuestionMapper;

	@Mock
	private GeminiService geminiService;

	@InjectMocks
	private AiQuestionService aiQuestionService;

	private User testUser;
	private Bike testBike;
	private Category testCategory;
	private AiQuestion testAiQuestion;
	private AiQuestionCreateRequest commonAiQuestionCreateRequest;
	private AiQuestionResponse commonAiQuestionResponse;

	@BeforeEach
	void setUp() {
		testUser = User.builder().id(1).nickname("testUser").build();
		testBike = Bike.builder().id(101).user(testUser).manufacturer("Honda").modelName("CBR250RR").build();
		testCategory = Category.builder().id(1).name("エンジン").displayOrder(1).build();
		testAiQuestion = AiQuestion.builder()
				.id(999)
				.user(testUser)
				.bike(testBike)
				.category(testCategory)
				.question("エンジンオイルの交換時期はいつですか？")
				.answer("エンジンオイルは3,000km～5,000kmまたは6ヶ月ごとに交換することをお勧めします。")
				.build();

		commonAiQuestionCreateRequest = AiQuestionCreateRequest.builder()
				.question("エンジンオイルの交換時期はいつですか？")
				.build();

		commonAiQuestionResponse = AiQuestionResponse.builder()
				.id(testAiQuestion.getId())
				.userId(testUser.getId())
				.bikeId(testBike.getId())
				.categoryId(testCategory.getId())
				.question("エンジンオイルの交換時期はいつですか？")
				.answer("エンジンオイルは3,000km～5,000kmまたは6ヶ月ごとに交換することをお勧めします。")
				.build();
	}

	private void stubUserFound() {
		when(userRepository.findByIdAndIsDeletedFalse(testUser.getId())).thenReturn(Optional.of(testUser));
	}

	private void stubUserNotFound() {
		when(userRepository.findByIdAndIsDeletedFalse(testUser.getId())).thenReturn(Optional.empty());
	}

	private void stubBikeFound() {
		when(bikeRepository.findByIdAndUserIdAndIsDeletedFalse(testUser.getId(), testBike.getId()))
				.thenReturn(Optional.of(testBike));
	}

	private void stubBikeNotFound() {
		when(bikeRepository.findByIdAndUserIdAndIsDeletedFalse(testUser.getId(), testBike.getId()))
				.thenReturn(Optional.empty());
	}

	private void stubCategoryFound() {
		when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
	}

	private void stubCategoryNotFound() {
		when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.empty());
	}

	@Nested
	class CreateAiQuestionTests {

		@Test
		void AI質問が正常に作成され作成されたAI質問情報が返されること() {
			stubUserFound();
			stubBikeFound();
			stubCategoryFound();

			when(geminiService.generateContent(any(AiQuestionPrompt.class))).thenReturn("Mocked AI Answer");

			when(aiQuestionMapper.toEntity(any(), any(), any(), any(), any())).thenReturn(testAiQuestion);
			when(aiQuestionMapper.toResponse(testAiQuestion)).thenReturn(commonAiQuestionResponse);

			when(aiQuestionRepository.save(testAiQuestion)).thenReturn(testAiQuestion);

			AiQuestionResponse result = aiQuestionService.createAiQuestion(commonAiQuestionCreateRequest,
					testUser.getId(),
					testBike.getId(),
					testCategory.getId());

			assertThat(result).isEqualTo(commonAiQuestionResponse);

			verify(userRepository).findByIdAndIsDeletedFalse(testUser.getId());
			verify(bikeRepository).findByIdAndUserIdAndIsDeletedFalse(testUser.getId(), testBike.getId());
			verify(categoryRepository).findById(testCategory.getId());
			verify(aiQuestionRepository).save(testAiQuestion);
			verify(aiQuestionMapper).toEntity(any(), any(), any(), any(), any());
			verify(aiQuestionMapper).toResponse(testAiQuestion);
			verify(geminiService,times(1)).generateContent(any(AiQuestionPrompt.class));
		}

		@Test
		void ユーザーが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubUserNotFound();

			assertThatThrownBy(() -> aiQuestionService.createAiQuestion(commonAiQuestionCreateRequest,
							testUser.getId(),
							testBike.getId(),
							testCategory.getId()))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("ユーザーID " + testUser.getId() + " が見つかりません。");

			verify(bikeRepository, never()).findByIdAndUserIdAndIsDeletedFalse(any(), any());
			verify(categoryRepository, never()).findById(any());
			verify(aiQuestionRepository, never()).save(any());
		}

		@Test
		void バイクが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubUserFound();
			stubBikeNotFound();

			assertThatThrownBy(() -> aiQuestionService.createAiQuestion(commonAiQuestionCreateRequest,
							testUser.getId(),
							testBike.getId(),
							testCategory.getId()))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("ユーザー ID " + testUser.getId() + " に紐づくバイクID " + testBike.getId() + "が見つかりません。");

			verify(categoryRepository, never()).findById(any());
			verify(aiQuestionRepository, never()).save(any());
		}

		@Test
		void カテゴリーが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubUserFound();
			stubBikeFound();
			stubCategoryNotFound();

			assertThatThrownBy(() -> aiQuestionService.createAiQuestion(commonAiQuestionCreateRequest,
					testUser.getId(),
					testBike.getId(),
					testCategory.getId()))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("カテゴリーID " + testCategory.getId() + " が見つかりません。");

			verify(aiQuestionRepository, never()).save(any());
		}
	}

	@Nested
	class GetAiQuestionByUserIdTests {

		@Test
		void ユーザーIDに紐づくAI質問履歴を正しく取得できること() {
			List<AiQuestion> aiQuestions = List.of(testAiQuestion);

			when(aiQuestionRepository.findByUserIdAndIsDeletedFalse(testUser.getId())).thenReturn(aiQuestions);
			when(aiQuestionMapper.toResponse(testAiQuestion)).thenReturn(commonAiQuestionResponse);

			List<AiQuestionResponse> result = aiQuestionService.getAiQuestionByUserId(testUser.getId());

			assertThat(result).hasSize(1);
			assertThat(result.getFirst()).isEqualTo(commonAiQuestionResponse);

			verify(aiQuestionRepository).findByUserIdAndIsDeletedFalse(testUser.getId());
			verify(aiQuestionMapper).toResponse(testAiQuestion);
		}

		@Test
		void ユーザーにAI質問履歴がない場合は空のリストを返すこと() {
			when(aiQuestionRepository.findByUserIdAndIsDeletedFalse(testUser.getId())).thenReturn(List.of());

			List<AiQuestionResponse> result = aiQuestionService.getAiQuestionByUserId(testUser.getId());

			assertThat(result).isEmpty();

			verify(aiQuestionRepository).findByUserIdAndIsDeletedFalse(testUser.getId());
		}
	}
}