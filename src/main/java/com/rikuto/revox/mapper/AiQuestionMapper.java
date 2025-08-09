package com.rikuto.revox.mapper;

import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.dto.aiquestion.AiQuestionCreateRequest;
import com.rikuto.revox.dto.aiquestion.AiQuestionResponse;
import com.rikuto.revox.domain.AiQuestion;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.Category;
import org.springframework.stereotype.Component;

/**
 * AiQuestionドメインと関連するDTO間のマッピングを行うクラスです。
 */
@Component
public class AiQuestionMapper {

	/**
	 * AiQuestionドメインをAiQuestionResponse DTOに変換します。
	 *
	 * @param aiQuestion 変換するAiQuestionドメイン
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
	 * リクエストDTOと関連ドメインから新しいAiQuestionドメインを作成します。
	 * @param request AI質問作成リクエスト
	 * @param user ユーザードメイン
	 * @param bike バイクドメイン
	 * @param category カテゴリードメイン
	 * @param answer AI生成回答
	 * @return 作成されたAiQuestionドメイン
	 */
	public AiQuestion toEntity(AiQuestionCreateRequest request,
	                           User user,
	                           Bike bike,
	                           Category category,
	                           String answer){

		return AiQuestion.builder()
				.user(user)
				.bike(bike)
				.category(category)

				.question(request.getQuestion())
				.answer(answer)
				.build();
	}
}
