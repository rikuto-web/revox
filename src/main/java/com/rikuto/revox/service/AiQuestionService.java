package com.rikuto.revox.service;

import com.rikuto.revox.dto.aiquestion.AiQuestionCreateRequest;
import com.rikuto.revox.dto.aiquestion.AiQuestionResponse;
import com.rikuto.revox.entity.AiQuestion;
import com.rikuto.revox.entity.Bike;
import com.rikuto.revox.entity.Category;
import com.rikuto.revox.entity.User;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.AiQuestionMapper;
import com.rikuto.revox.repository.AiQuestionRepository;
import com.rikuto.revox.repository.BikeRepository;
import com.rikuto.revox.repository.CategoryRepository;
import com.rikuto.revox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AiQuestionService {

	private final AiQuestionRepository aiQuestionRepository;
	private final UserRepository userRepository;
	private final BikeRepository bikeRepository;
	private final CategoryRepository categoryRepository;
	private final AiQuestionMapper aiQuestionMapper;

	public AiQuestionService(AiQuestionRepository aiQuestionRepository, UserRepository userRepository, BikeRepository bikeRepository, CategoryRepository categoryRepository, AiQuestionMapper aiQuestionMapper) {
		this.aiQuestionRepository = aiQuestionRepository;
		this.userRepository = userRepository;
		this.bikeRepository = bikeRepository;
		this.categoryRepository = categoryRepository;
		this.aiQuestionMapper = aiQuestionMapper;
	}


	@Transactional
	public AiQuestionResponse createAiQuestion(AiQuestionCreateRequest request){
		//Entityの存在確認
		User user = userRepository.findByIdAndIsDeletedFalse(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーID " + request.getUserId() + " が見つかりません。"));
		Bike bike = bikeRepository.findByUserIdAndIsDeletedFalse(request.getBikeId())
				.orElseThrow(() -> new ResourceNotFoundException("バイクID " + request.getBikeId() + " が見つかりません。"));
		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリーID " + request.getCategoryId() + " が見つかりません。"));

//ToDo		ここに動的に回答を生成させる
		String aiAnswer = generateAiAnswer(request.getQuestion(), bike, category);

		AiQuestion aiQuestion = aiQuestionMapper.toEntity(request, user, bike ,category, aiAnswer);
		AiQuestion savedAiQuestion = aiQuestionRepository.save(aiQuestion);

		return aiQuestionMapper.toResponse(savedAiQuestion);
	}

	/**
	 * ユーザーのAI質問・回答履歴を取得します。
	 * @param userId ユーザーID
	 * @return AI質問・回答履歴リスト
	 */
	public List<AiQuestionResponse> getAiQuestionByUserId(Integer userId) {
		List<AiQuestion> aiQuestions = aiQuestionRepository.findByUserIdAndIsDeletedFalse(userId);
		return aiQuestions.stream()
				.map(aiQuestionMapper::toResponse)
				.toList();
	}



//	ToDO 外部APIを今後導入
	private String generateAiAnswer(String question, Bike bike, Category category) {
		// 静的な回答（後で外部API呼び出しに変更）
		return String.format(
				"【%s】%s %sに関するご質問ですね。\n\n" +
						"必要な物品・工具：\n" +
						"- 専用部品 x1\n" +
						"- 工具セット\n" +
						"- 作業用手袋\n\n" +
						"作業手順：\n" +
						"1. 安全確認\n" +
						"2. 部品交換\n" +
						"3. 動作確認\n\n" +
						"※詳細は整備マニュアルをご確認ください。",
				category.getName(),
				bike.getManufacturer(),
				bike.getModelName()
		);
	}
























}
