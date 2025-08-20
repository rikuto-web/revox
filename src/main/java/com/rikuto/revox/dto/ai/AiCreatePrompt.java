package com.rikuto.revox.dto.ai;

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
public class AiCreatePrompt {
	private String manufacturer;
	private String modelName;
	private String modelCode;
	private Integer modelYear;

	private String question;
}
