package com.rikuto.revox.dto.auth;

import com.rikuto.revox.dto.user.UserResponse;
import lombok.Builder;
import lombok.Getter;

/**
 * ログイン結果をフロントへ渡すためのDTOです。
 */
@Getter
@Builder
public class LoginResponse {
	private final String accessToken;
	private final String tokenType;
	private final UserResponse user;
}