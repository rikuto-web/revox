package com.rikuto.revox.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "AIへの質問を行うためのリクエストDTOです。")
public class AiQuestionCreateRequest {

	@NotBlank(message = "質問内容は必須です。")
	@Size(max = 1000, message = "質問内容は1000文字以内で入力してください。")
	@Schema(description = "AIへの質問内容。", requiredMode = Schema.RequiredMode.REQUIRED, example = "バイクのオイル交換の手順を教えてください。")
	private String question;
}