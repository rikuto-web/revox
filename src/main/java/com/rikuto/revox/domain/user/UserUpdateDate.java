package com.rikuto.revox.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * ユーザー更新リクエストDTO。
 * ニックネームのみ更新可能です。
 */
@Schema(description = "ユーザーの更新情報を表すドメイン")
@Getter
@Setter
@Builder
public class UserUpdateDate {
	private String nickname;
}