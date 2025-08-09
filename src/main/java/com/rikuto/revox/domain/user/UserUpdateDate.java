package com.rikuto.revox.domain.user;

import jakarta.validation.constraints.NotBlank;
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
public class UserUpdateDate {
	private String nickname;
}