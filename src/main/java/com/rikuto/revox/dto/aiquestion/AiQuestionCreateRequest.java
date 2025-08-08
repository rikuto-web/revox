package com.rikuto.revox.dto.aiquestion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AIへの質問を行うためのリクエストDTOです。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiQuestionCreateRequest {

	@NotBlank(message = "質問内容は必須です。")
	@Size(max = 1000, message = "質問内容は1000文字以内で入力してください。")
	private String question;
}
