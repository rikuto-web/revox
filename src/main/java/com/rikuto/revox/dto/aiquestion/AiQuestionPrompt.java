package com.rikuto.revox.dto.aiquestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * AIへのプロンプト生成に必要な情報を保持するDTOです。
 * バリデーションは、このDTOにデータを渡す前の段階で完了していることを前提としています。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiQuestionPrompt {

	private String question;

	private String manufacturer;
	private String modelName;
	private String modelCode;
	private Integer modelYear;
	private Integer currentMileage;
	private LocalDate purchaseDate;

	private Integer categoryId;
}
