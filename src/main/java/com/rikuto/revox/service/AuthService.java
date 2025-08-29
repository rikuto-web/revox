package com.rikuto.revox.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.rikuto.revox.domain.User;
import com.rikuto.revox.dto.auth.GoogleTokenPayload;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.exception.AuthenticationException;
import com.rikuto.revox.mapper.LoginResponseMapper;
import com.rikuto.revox.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * 外部認証に関するビジネスロジックを処理するサービスクラスです。
 * 認証およびJWTトークンの生成を行います。
 */
@Slf4j
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
		log.info("Google IDトークンを使用してログイン処理を開始します。");
		GoogleTokenPayload payload = verifyGoogleIdToken(googleIdToken);
		if(payload.getEmail() == null) {
			log.warn("Googleアカウントのメールアドレスがnullでした。");
			throw new AuthenticationException("Googleアカウントのメールアドレスが取得できません。");
		}
		log.info("ユーザーの検索または新規登録を開始します。");
		User user = userService.findOrCreateUser(
				payload.getSub(),
				payload.getName(),
				payload.getEmail()
		);
		log.warn("正常に処理されました。");

		String accessToken = jwtTokenProvider.generateToken(user.getUniqueUserId(), user.getRoles());
		log.info("JWTアクセストークンが正常に生成されました。");

		return loginResponseMapper.toLoginResponse(user, accessToken);
	}

	/**
	 * Google IDトークンを検証し、ペイロードを抽出するヘルパーメソッドです。
	 *
	 * @param googleIdToken Googleから受け取ったIDトークン文字列
	 * @return ペイロード情報を含むDTO
	 * @throws AuthenticationException トークンが無効な場合にスロー
	 */
	private GoogleTokenPayload verifyGoogleIdToken(String googleIdToken) {
		log.info("Google IDトークンの検証を開始します。");
		try {
			GoogleIdToken idToken = googleIdTokenVerifier.verify(googleIdToken);
			if(idToken != null) {
				GoogleIdToken.Payload payload = idToken.getPayload();

				String name = (String) payload.get("name");
				if(name == null || name.isEmpty()) {
					log.warn("Googleアカウントから名前が取得できませんでした。デフォルトを設定します。");
					name = "名無しさん";
				}

				log.info("Google IDトークンの検証に成功しました。");
				return GoogleTokenPayload.builder()
						.sub(payload.getSubject())
						.email(payload.getEmail())
						.name(name)
						.build();
			} else {
				log.warn("IDトークンがnullでした。無効なGoogle IDトークンです。");
				throw new AuthenticationException("無効なGoogle IDトークンです。");
			}
		} catch(GeneralSecurityException | IOException e) {
			log.error("Google IDトークンの検証中に例外が発生しました。");
			throw new AuthenticationException("Google IDトークンの検証に失敗しました。", e);
		}
	}
}