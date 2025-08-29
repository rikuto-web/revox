package com.rikuto.revox.dto.auth;

import com.rikuto.revox.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ログイン結果をフロントへ渡すためのDTOです。
 */
@Getter
@Builder
@Schema(description = "ログイン成功時に返されるレスポンスDTOです。")
public class LoginResponse {

	@Schema(description = "認証に使用するJWTアクセストークン。")
	private final String accessToken;

	@Schema(description = "トークンのタイプ。通常は'Bearer'です。")
	private final String tokenType;

	@Schema(description = "認証されたユーザーの詳細情報。")
	private final UserResponse user;

	@Schema(description = "レコードが作成された日時。")
	private LocalDateTime createdAt;

	@Schema(description = "レコードが更新された最終日時。")
	private LocalDateTime updatedAt;
}