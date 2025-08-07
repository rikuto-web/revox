package com.rikuto.revox.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Google認証リクエストDTOです。
 */
public record LoginRequest(

		@NotBlank(message = "Google IDトークンは必須です。")
		String idToken
) {}