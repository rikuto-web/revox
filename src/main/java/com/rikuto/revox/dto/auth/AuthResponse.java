package com.rikuto.revox.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 認証成功後のレスポンスDTOです。
 */
@Getter
@Builder
@Schema(description = "認証成功後に返されるレスポンスDTOです。JWTトークンを含みます。")
public class AuthResponse {

	@Schema(description = "認証に成功した際に発行されるJWTトークン。")
	private String token;
}