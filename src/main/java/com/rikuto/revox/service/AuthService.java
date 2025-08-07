package com.rikuto.revox.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.rikuto.revox.domain.User;
import com.rikuto.revox.dto.auth.GoogleTokenPayload;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.exception.AuthenticationException;
import com.rikuto.revox.mapper.LoginResponseMapper;
import com.rikuto.revox.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * 外部認証に関するビジネスロジックを処理するサービスクラスです。
 * 認証およびJWTトークンの生成を行います。
 */
@Service
public class AuthService {

	private final UserService userService;

	private final JwtTokenProvider jwtTokenProvider;
	private final LoginResponseMapper loginResponseMapper;

	private final String googleClientId;

	public AuthService(UserService userService,
	                   JwtTokenProvider jwtTokenProvider,
	                   LoginResponseMapper loginResponseMapper,
	                   @Value("${google.client-id}") String googleClientId) {
		this.userService = userService;
		this.loginResponseMapper = loginResponseMapper;
		this.jwtTokenProvider = jwtTokenProvider;
		this.googleClientId = googleClientId;
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
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
				.setAudience(Collections.singletonList(googleClientId))
				.build();
		try {
			GoogleIdToken idToken = verifier.verify(idTokenString);
			if(idToken != null) {
				GoogleIdToken.Payload payload = idToken.getPayload();

				return GoogleTokenPayload.builder()
						.sub(payload.getSubject())
						.email(payload.getEmail())
						.name((String) payload.get("name"))
						.build();
			} else {
				// ここでトークンが無効な理由が分からないことが多いのでログ出力する
				System.err.println("Google IDトークンの検証に失敗しました。トークン: " + idTokenString);
				throw new AuthenticationException("無効なGoogle IDトークンです。");
			}
		} catch (GeneralSecurityException | IOException e) {
			System.err.println("Google IDトークンの検証時に例外発生。トークン: " + idTokenString);
			e.printStackTrace();
			throw new AuthenticationException("Google IDトークンの検証に失敗しました。", e);
		}
	}
}