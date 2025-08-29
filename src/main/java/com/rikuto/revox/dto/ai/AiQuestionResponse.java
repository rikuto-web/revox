package com.rikuto.revox.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AIへの質問および回答に対するレスポンス内容のDTOです。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AIへの質問とその回答を表すレスポンスDTOです。")
public class AiQuestionResponse {

	@Schema(description = "AI質問の一意なID。")
	private Integer id;

	@Schema(description = "この質問を投稿したユーザーの一意なID。")
	private Integer userId;

	@Schema(description = "質問に関連付けられたバイクの一意なID。")
	private Integer bikeId;

	@Schema(description = "質問に関連付けられたカテゴリーの一意なID。")
	private Integer categoryId;

	@Schema(description = "ユーザーが入力した質問内容。")
	private String question;

	@Schema(description = "AIが生成した回答内容。")
	private String answer;

	@Schema(description = "レコードが作成された日時。")
	private LocalDateTime createdAt;
}