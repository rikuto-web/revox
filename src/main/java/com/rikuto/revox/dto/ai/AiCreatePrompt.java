package com.rikuto.revox.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AIへのプロンプト生成に必要な情報を保持するDTOです。
 * このDTOにデータを渡す前の段階で、バリデーションは完了していることを前提としています。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AIへのプロンプト生成に必要な情報を表すDTOです。")
public class AiCreatePrompt {

	@Schema(description = "バイクのメーカー名。", example = "ホンダ")
	private String manufacturer;

	@Schema(description = "バイクの車両名。", example = "Rebel 250")
	private String modelName;

	@Schema(description = "バイクの型式。")
	private String modelCode;

	@Schema(description = "バイクの年式。", example = "2023")
	private Integer modelYear;

	@Schema(description = "ユーザーが入力した質問内容。", example = "キャブレターの清掃方法について教えてください。")
	private String question;
}