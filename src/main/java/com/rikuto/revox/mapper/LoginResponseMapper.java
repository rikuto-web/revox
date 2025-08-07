package com.rikuto.revox.mapper;

import com.rikuto.revox.domain.User;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.dto.user.UserResponse;
import org.springframework.stereotype.Component;

/**
 * 外部認証専用のLoginResponseMapperです。
 */
@Component
public class LoginResponseMapper {

	private final UserResponseMapper userResponseMapper;
	public LoginResponseMapper(UserResponseMapper userResponseMapper) {
		this.userResponseMapper = userResponseMapper;
	}

	/**
	 * AuthUserドメインをLoginResponseに変換します。
	 * 認証成功時のユーザー情報をフロントへ渡すためのレスポンスマッパーです。
	 *
	 * @param user ユーザー情報
	 * @param accessToken 外部認証のJWTトークン
	 * @return 認証成功時のユーザー情報
	 */
	public LoginResponse toLoginResponse(User user, String accessToken) {
		UserResponse userResponse = userResponseMapper.toResponse(user);

		return LoginResponse.builder()
				.accessToken(accessToken)
				.tokenType("Bearer")
				.user(userResponse)
				.build();
	}
}
