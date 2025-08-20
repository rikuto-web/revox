package com.rikuto.revox.mapper;

import com.rikuto.revox.dto.ai.AiQuestionResponse;
import com.rikuto.revox.domain.Ai;
import org.springframework.stereotype.Component;

/**
 * AiQuestionドメインと関連するDTO間のマッピングを行うクラスです。
 */
@Component
public class AiMapper {

	/**
	 * AiドメインをAiQuestionResponse DTOに変換します。
	 *
	 * @param ai 変換するAiドメイン
	 * @return 変換されたAiQuestionResponse DTO
	 */
	public AiQuestionResponse toResponse(Ai ai) {

		return AiQuestionResponse.builder()
				.id(ai.getId())
				.userId(ai.getUser().getId())
				.bikeId(ai.getBike().getId())
				.categoryId(ai.getCategory().getId())

				.question(ai.getQuestion())
				.answer(ai.getAnswer())

				.createdAt(ai.getCreatedAt())
				.build();
	}
}
