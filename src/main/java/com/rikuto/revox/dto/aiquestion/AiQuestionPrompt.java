package com.rikuto.revox.dto.aiquestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * AIへバイク情報を渡すためのDTOです。
 * DBへ登録時に入力チェックを行っているため、このDTOにはバリデーションはありません。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiQuestionPrompt {

	private String manufacturer;
	private String modelName;
	private String modelCode;
	private Integer modelYear;
	private Integer currentMileage;
	private LocalDate purchaseDate;

	private String question;

	private Integer categoryId;

}
