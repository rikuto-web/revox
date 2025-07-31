package com.rikuto.revox.dto.aiquestion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiQuestionCreateRequest {

	@NotNull(message = "ユーザーIDは必須です。")
	@Min(value = 1 ,message = "ユーザーIDは1以上である必要があります。")
	private Integer userId;

	@NotNull(message = "バイクIDは必須です。")
	@Min(value = 1, message = "バイクIDは1以上である必要があります。")
	private Integer bikeId;

	@NotNull(message = "カテゴリーIDは必須です。")
	@Min(value = 1, message = "カテゴリーIDは1以上である必要があります。")
	private Integer categoryId;

	@NotBlank(message = "質問内容は必須です。")
	@Size(max = 1000, message = "質問内容は1000文字以内で入力してください。")
	private String question;





}
