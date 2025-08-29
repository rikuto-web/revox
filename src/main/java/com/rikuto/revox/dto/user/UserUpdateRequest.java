package com.rikuto.revox.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/**
 * ユーザー情報を更新する際に受け取るリクエストです。
 * 各フィールドにはバリデーションがあります。
 */
@Getter
@Builder
@Schema(description = "ユーザー情報を更新するためのリクエストDTOです。")
public class UserUpdateRequest {

	@NotBlank(message = "ユーザー名は必須です。")
	@Size(max = 50, message = "50文字以内で入力してください。")
	@Schema(description = "更新するユーザーの新しいニックネーム。")
	private String nickname;
}