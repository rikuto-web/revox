package com.rikuto.revox.service;

import com.rikuto.revox.domain.Ai;
import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.dto.ai.AiCreatePrompt;
import com.rikuto.revox.dto.ai.AiQuestionCreateRequest;
import com.rikuto.revox.dto.ai.AiQuestionResponse;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.AiMapper;
import com.rikuto.revox.repository.AiRepository;
import com.rikuto.revox.repository.BikeRepository;
import com.rikuto.revox.repository.CategoryRepository;
import com.rikuto.revox.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AIに関するビジネスロジックを処理するサービスクラスです。
 */
@Slf4j
@Service
public class AiService {

	private final AiRepository aiRepository;
	private final UserRepository userRepository;
	private final BikeRepository bikeRepository;
	private final CategoryRepository categoryRepository;

	private final AiMapper aiMapper;

	private final GeminiService geminiService;

	public AiService(AiRepository aiRepository,
	                 UserRepository userRepository,
	                 BikeRepository bikeRepository,
	                 CategoryRepository categoryRepository,
	                 AiMapper aiMapper,
	                 GeminiService geminiService) {
		this.aiRepository = aiRepository;
		this.userRepository = userRepository;
		this.bikeRepository = bikeRepository;
		this.categoryRepository = categoryRepository;
		this.aiMapper = aiMapper;
		this.geminiService = geminiService;
	}

	// CREATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * AIへの質問に対する回答を同期で生成します。
	 * ユーザーと紐づく単一のバイク情報とカテゴリー情報を渡して回答を生成します。
	 *
	 * @param request    AIへの質問
	 * @param userId     ユーザーID
	 * @param bikeId     バイクID
	 * @param categoryId カテゴリーID
	 */
	@Transactional
	public AiQuestionResponse createAiQuestion(AiQuestionCreateRequest request,
	                                           Integer userId,
	                                           Integer bikeId,
	                                           Integer categoryId) {
		log.info("各種IDで検索を開始します。");
		User user = userRepository.findByIdAndIsDeletedFalse(userId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーID " + userId + " が見つかりません。"));
		log.info("ユーザーIDでの検索が正常に実行されました。");

		Bike bike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(bikeId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザー ID " + userId + " に紐づくバイクID " + bikeId + "が見つかりません。"));
		log.info("バイクIDでの検索が正常に実行されました。");

		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリーID " + categoryId + " が見つかりません。"));
		log.info("カテゴリーIDでの検索が正常に実行されました。");

		log.info("プロンプトを生成しAIの回答生成を開始します。");
		AiCreatePrompt createQuestion = AiCreatePrompt.builder()
				.question(request.getQuestion())

				.manufacturer(bike.getManufacturer())
				.modelName(bike.getModelName())
				.modelCode(bike.getModelCode())
				.modelYear(bike.getModelYear())
				.build();

		String answer = geminiService.generateContent(createQuestion);
		if(answer == null || answer.isBlank()) {
			log.warn("質問に対して空の回答が返されました。 Prompt={}", createQuestion);
		}

		Ai answerToDomain = Ai.builder()
				.user(user)
				.bike(bike)
				.category(category)

				.question(createQuestion.getQuestion())
				.answer(answer)
				.build();

		Ai savedAnswer = aiRepository.save(answerToDomain);
		log.info("AIからの回答を正常に登録できました。");

		return aiMapper.toResponse(savedAnswer);
	}


	// READ
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * ユーザーのAI履歴を全件取得します。
	 *
	 * @param userId ユーザーID
	 * @return AI質問・回答履歴リスト
	 */
	@Transactional(readOnly = true)
	public List<AiQuestionResponse> getAiQuestionByUserId(Integer userId) {
		List<Ai> questionList = aiRepository.findByUserId(userId);

		return questionList.stream()
				.map(aiMapper::toResponse)
				.toList();
	}
}
