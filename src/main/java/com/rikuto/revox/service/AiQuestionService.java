package com.rikuto.revox.service;

import com.rikuto.revox.dto.aiquestion.AiQuestionCreateRequest;
import com.rikuto.revox.dto.aiquestion.AiQuestionPrompt;
import com.rikuto.revox.dto.aiquestion.AiQuestionResponse;
import com.rikuto.revox.domain.AiQuestion;
import com.rikuto.revox.domain.Bike;
import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.User;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.AiQuestionMapper;
import com.rikuto.revox.repository.AiQuestionRepository;
import com.rikuto.revox.repository.BikeRepository;
import com.rikuto.revox.repository.CategoryRepository;
import com.rikuto.revox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AIに関するビジネスロジックを処理するサービスクラスです。
 */
@Service
public class AiQuestionService {

	private final AiQuestionRepository aiQuestionRepository;
	private final UserRepository userRepository;
	private final BikeRepository bikeRepository;
	private final CategoryRepository categoryRepository;
	private final AiQuestionMapper aiQuestionMapper;
	private final GeminiService geminiService;

	public AiQuestionService(AiQuestionRepository aiQuestionRepository, UserRepository userRepository, BikeRepository bikeRepository, CategoryRepository categoryRepository, AiQuestionMapper aiQuestionMapper, GeminiService geminiService) {
		this.aiQuestionRepository = aiQuestionRepository;
		this.userRepository = userRepository;
		this.bikeRepository = bikeRepository;
		this.categoryRepository = categoryRepository;
		this.aiQuestionMapper = aiQuestionMapper;
		this.geminiService = geminiService;
	}

	/**
	 * AIへの質問に対する回答を生成します。
	 * ユーザーと紐づく単一のバイク情報とカテゴリー情報を渡して回答を生成します。
	 * 現在固定の回答へ返答する設計になっており、今後外部APIの連携を予定しています。
	 */
	@Transactional
	public AiQuestionResponse createAiQuestion(AiQuestionCreateRequest request){

		User user = userRepository.findByIdAndIsDeletedFalse(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"ユーザーID " + request.getUserId() + " が見つかりません。"));

		Bike bike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(request.getUserId(), request.getBikeId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"ユーザー ID " + request.getUserId() + " に紐づくバイクID " + request.getBikeId() + "が見つかりません。"));

		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"カテゴリーID " + request.getCategoryId() + " が見つかりません。"));

		AiQuestionPrompt promptDto = AiQuestionPrompt.builder()
				.question(request.getQuestion())
				.categoryId(request.getCategoryId())

				.manufacturer(bike.getManufacturer())
				.modelName(bike.getModelName())
				.modelCode(bike.getModelCode())
				.modelYear(bike.getModelYear())
				.currentMileage(bike.getCurrentMileage())
				.purchaseDate(bike.getPurchaseDate())
				.build();

		String aiAnswer = geminiService.generateContent(promptDto);

		AiQuestion aiQuestion = aiQuestionMapper.toEntity(request, user, bike ,category, aiAnswer);

		AiQuestion savedAiQuestion = aiQuestionRepository.save(aiQuestion);

		return aiQuestionMapper.toResponse(savedAiQuestion);
	}

	/**
	 * ユーザーのAI質問・回答履歴を取得します。
	 *
	 * @param userId ユーザーID
	 * @return AI質問・回答履歴リスト
	 */
	@Transactional(readOnly = true)
	public List<AiQuestionResponse> getAiQuestionByUserId(Integer userId) {

		List<AiQuestion> aiQuestions = aiQuestionRepository.findByUserIdAndIsDeletedFalse(userId);

		return aiQuestions.stream()
				.map(aiQuestionMapper::toResponse)
				.toList();
	}
}
