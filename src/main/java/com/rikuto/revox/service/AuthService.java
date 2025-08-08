package com.rikuto.revox.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.dto.auth.GoogleTokenPayload;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.exception.AuthenticationException;
import com.rikuto.revox.mapper.LoginResponseMapper;
import com.rikuto.revox.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * 外部認証に関するビジネスロジックを処理するサービスクラスです。
 * 認証およびJWTトークンの生成を行います。
 */
@Service
public class AuthService {

	private final UserService userService;

	private final JwtTokenProvider jwtTokenProvider;
	private final LoginResponseMapper loginResponseMapper;

	private final GoogleIdTokenVerifier googleIdTokenVerifier;

	public AuthService(UserService userService,
	                   JwtTokenProvider jwtTokenProvider,
	                   LoginResponseMapper loginResponseMapper,
	                   GoogleIdTokenVerifier googleIdTokenVerifier) {
		this.userService = userService;
		this.loginResponseMapper = loginResponseMapper;
		this.jwtTokenProvider = jwtTokenProvider;
		this.googleIdTokenVerifier = googleIdTokenVerifier;
	}

	/**
	 * Google認証でのログイン処理を行います。
	 * 検索後ユーザー情報がない場合登録を行います。
	 *
	 * @param googleIdToken Google IDトークン
	 * @return ログインレスポンス
	 */
	public LoginResponse loginWithGoogle(String googleIdToken) {
		GoogleTokenPayload idToken = verifyGoogleIdToken(googleIdToken);
		if(idToken.getEmail() == null) {
			throw new AuthenticationException("Googleアカウントのメールアドレスが取得できません。");
		}
		User user = userService.findOrCreateUser(
				idToken.getSub(),
				idToken.getName(),
				idToken.getEmail()
		);

		String token = jwtTokenProvider.generateToken(user.getUniqueUserId());

		return loginResponseMapper.toLoginResponse(user, token);
	}

	/**
	 * Google IDトークンを検証し、ペイロードを抽出するヘルパーメソッドです。
	 *
	 * @param idTokenString Googleから受け取ったIDトークン文字列
	 * @return ペイロード情報を含むDTO
	 * @throws AuthenticationException トークンが無効な場合にスロー
	 */
	private GoogleTokenPayload verifyGoogleIdToken(String idTokenString) {
		try {
			GoogleIdToken idToken = googleIdTokenVerifier.verify(idTokenString);
			if(idToken != null) {
				GoogleIdToken.Payload payload = idToken.getPayload();

				String name = (String) payload.get("name");
				if(name == null || name.isEmpty()) {
					name = "名無しさん";
				}

				return GoogleTokenPayload.builder()
						.sub(payload.getSubject())
						.email(payload.getEmail())
						.name(name)
						.build();
			} else {
				throw new AuthenticationException("無効なGoogle IDトークンです。");
			}
		} catch(GeneralSecurityException | IOException e) {
			throw new AuthenticationException("Google IDトークンの検証に失敗しました。", e);
		}
	}
}