package com.rikuto.revox.mapper;

import com.rikuto.revox.dto.aiquestion.AiQuestionCreateRequest;
import com.rikuto.revox.dto.aiquestion.AiQuestionResponse;
import com.rikuto.revox.entity.AiQuestion;
import com.rikuto.revox.entity.Bike;
import com.rikuto.revox.entity.Category;
import com.rikuto.revox.entity.User;
import com.rikuto.revox.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * AiQuestionエンティティと関連するDTO間のマッピングを行うクラスです。
 */
@Component
public class AiQuestionMapper {

	/**
	 * AiQuestionエンティティをAiQuestionResponse DTOに変換します。
	 *
	 * @param aiQuestion 変換するAiQuestionエンティティ
	 * @return 変換されたAiQuestionResponse DTO
	 */
	public AiQuestionResponse toResponse(AiQuestion aiQuestion) {
		return AiQuestionResponse.builder()
				.id(aiQuestion.getId())
				.userId(aiQuestion.getUser().getId())
				.bikeId(aiQuestion.getBike().getId())
				.categoryId(aiQuestion.getCategory().getId())
				.question(aiQuestion.getQuestion())
				.answer(aiQuestion.getAnswer())
				.createdAt(aiQuestion.getCreatedAt())
				.updatedAt(aiQuestion.getUpdatedAt())
				.build();
	}

	/**
	 * リクエストDTOと関連エンティティから新しいAiQuestionエンティティを作成します。
	 * @param request AI質問作成リクエスト
	 * @param user ユーザーエンティティ
	 * @param bike バイクエンティティ
	 * @param category カテゴリーエンティティ
	 * @param answer AI生成回答
	 * @return 作成されたAiQuestionエンティティ
	 */
	public AiQuestion toEntity(AiQuestionCreateRequest request, User user, Bike bike, Category category, String answer){
		LocalDateTime now = LocalDateTime.now();
		return AiQuestion.builder()
				.user(user)
				.bike(bike)
				.category(category)
				.question(request.getQuestion())
				.answer(answer)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}
}
