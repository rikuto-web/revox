package com.rikuto.revox.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * ユーザー更新リクエストDTO。
 * ニックネームのみ更新可能です。
 */
@Getter
@Setter
@Builder
public class UserUpdateRequest {
	@Size(max = 50, message = "ニックネームは50文字以内で入力してください。")
	private String nickname;
}