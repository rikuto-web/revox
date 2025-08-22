package com.rikuto.revox.controller;

import com.rikuto.revox.dto.auth.AuthResponse;
import com.rikuto.revox.dto.auth.LoginRequest;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.security.jwt.JwtTokenProvider;
import com.rikuto.revox.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 外部認証のコントローラーです。
 * Google認証以外も拡張予定です。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthUserController {

	private final AuthService authService;
	private final JwtTokenProvider jwtTokenProvider;

	public AuthUserController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
		this.authService = authService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	/**
	 * Google認証でのログイン
	 * post /api/auth/google
	 *
	 * @param request Google IDトークンを含むリクエスト
	 * @return JWTトークンとユーザー情報とHTTPステータス200 OK
	 */
	@PostMapping("/google")
	public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody @Valid LoginRequest request) {
		LoginResponse response = authService.loginWithGoogle(request.getIdToken());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/guest")
	public ResponseEntity<AuthResponse> guestLogin() {
		String guestId = "guest-user";
		String guestRole = "ROLE_GUEST";

		String token = jwtTokenProvider.generateToken(guestId, guestRole);

		return ResponseEntity.ok(AuthResponse.builder().token(token).build());
	}
}