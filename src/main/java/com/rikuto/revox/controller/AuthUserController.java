package com.rikuto.revox.controller;

import com.rikuto.revox.dto.auth.LoginRequest;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部認証のコントローラーです。
 * Google認証以外も拡張予定です。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthUserController {

	private final AuthService authService;

	public AuthUserController(AuthService authService) {
		this.authService = authService;
	}

	/**
	 * Google認証でのログイン
	 * post /api/auth/google
	 *
	 * @param request Google IDトークンを含むリクエスト
	 * @return JWTトークンとユーザー情報
	 */
	@PostMapping("/google")
	public ResponseEntity<LoginResponse> loginWithGoogle(@Valid @RequestBody LoginRequest request) {
		LoginResponse response = authService.loginWithGoogle(request.idToken());

		return ResponseEntity.ok(response);
	}
}