package com.rikuto.revox.mapper;

import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.dto.user.UserResponse;
import org.springframework.stereotype.Component;

/**
 * 認証成功時のユーザー情報とJWTトークンを、ログインレスポンスDTOにマッピングするクラスです。
 * 変換した後、アクセストークンと組み合わせて最終的なログインレスポンスを作成します。
 */
@Component
public class LoginResponseMapper {

	private final UserResponseMapper userResponseMapper;

	public LoginResponseMapper(UserResponseMapper userResponseMapper) {
		this.userResponseMapper = userResponseMapper;
	}

	/**
	 * ユーザー情報とアクセストークンをLoginResponseに変換します。
	 *
	 * @param user        認証されたユーザー情報
	 * @param accessToken JWTトークン
	 * @return 認証成功時のログインレスポンスDTO
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
