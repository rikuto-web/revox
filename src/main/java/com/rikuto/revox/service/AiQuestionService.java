package com.rikuto.revox.service;

import com.rikuto.revox.dto.aiquestion.AiQuestionCreateRequest;
import com.rikuto.revox.dto.aiquestion.AiQuestionPrompt;
import com.rikuto.revox.dto.aiquestion.AiQuestionResponse;
import com.rikuto.revox.domain.AiQuestion;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.user.User;
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
	private final BikeRepository bikeRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;

	private final AiQuestionMapper aiQuestionMapper;

	private final GeminiService geminiService;

	public AiQuestionService(AiQuestionRepository aiQuestionRepository,
	                         BikeRepository bikeRepository,
	                         CategoryRepository categoryRepository,
	                         UserRepository userRepository,
	                         AiQuestionMapper aiQuestionMapper,
	                         GeminiService geminiService) {
		this.aiQuestionRepository = aiQuestionRepository;
		this.bikeRepository = bikeRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.aiQuestionMapper = aiQuestionMapper;
		this.geminiService = geminiService;
	}

	// CREATE
	//------------------------------------------------------------------------------------------------------------------
	/**
	 * AIへの質問に対する回答を生成します。
	 * ユーザーと紐づく単一のバイク情報とカテゴリー情報を渡して回答を生成します。
	 */
	@Transactional
	public AiQuestionResponse createAiQuestion(AiQuestionCreateRequest request,
	                                           Integer userId,
                                               Integer bikeId,
                                               Integer categoryId
	                                           ){

		User user = userRepository.findByIdAndIsDeletedFalse(userId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーID " + userId + " が見つかりません。"));

		Bike bike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(userId, bikeId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザー ID " + userId+ " に紐づくバイクID " + bikeId+ "が見つかりません。"));

		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリーID " + categoryId + " が見つかりません。"));

		AiQuestionPrompt prompt = AiQuestionPrompt.builder()
				.question(request.getQuestion())

				.manufacturer(bike.getManufacturer())
				.modelName(bike.getModelName())
				.modelCode(bike.getModelCode())
				.modelYear(bike.getModelYear())
				.currentMileage(bike.getCurrentMileage())
				.purchaseDate(bike.getPurchaseDate())
				.build();

		String aiAnswer = geminiService.generateContent(prompt);

		AiQuestion aiConversation = aiQuestionMapper.toEntity(request, user, bike ,category, aiAnswer);

		AiQuestion savedAiConversation = aiQuestionRepository.save(aiConversation);

		return aiQuestionMapper.toResponse(savedAiConversation);
	}

	// READ
	//------------------------------------------------------------------------------------------------------------------
	/**
	 * ユーザーのAI履歴を取得します。
	 *
	 * @param userId ユーザーID
	 * @return AI質問・回答履歴リスト
	 */
	@Transactional(readOnly = true)
	public List<AiQuestionResponse> getAiQuestionByUserId(Integer userId) {

		List<AiQuestion> aiConversation = aiQuestionRepository.findByUserIdAndIsDeletedFalse(userId);

		return aiConversation.stream()
				.map(aiQuestionMapper::toResponse)
				.toList();
	}
}
