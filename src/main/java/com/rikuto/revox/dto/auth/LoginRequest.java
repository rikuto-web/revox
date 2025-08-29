package com.rikuto.revox.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Google認証リクエストDTOです。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Google認証のためのリクエストDTOです。IDトークンを含みます。")
public class LoginRequest {

	@NotBlank(message = "Google IDトークンは必須です。")
	@Schema(description = "Googleから取得したIDトークン。JWT形式の文字列。", requiredMode = Schema.RequiredMode.REQUIRED,
			example = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij...<truncated>...")
	private String idToken;
}