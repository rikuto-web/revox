package com.rikuto.revox.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateRequest {

	@NotBlank(message = "ユーザー名は必須です。")
	@Size(max = 50, message = "50文字以内で入力してください。")
	private String nickname;
}
