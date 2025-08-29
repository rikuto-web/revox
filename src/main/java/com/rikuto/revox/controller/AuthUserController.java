package com.rikuto.revox.controller;

import com.rikuto.revox.dto.auth.AuthResponse;
import com.rikuto.revox.dto.auth.LoginRequest;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.security.jwt.JwtTokenProvider;
import com.rikuto.revox.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部認証のコントローラーです。
 * Google認証以外も拡張予定です。
 */
@Tag(name = "外部認証に関する管理", description = "Google認証やゲストログインなどの外部認証を管理するエンドポイント群です。")
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
	 */
	@Operation(summary = "Google認証でログインする", description = "Google IDトークンを使用して認証・ログインを行い、JWTトークンとユーザー情報を取得します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "認証成功、JWTトークンとユーザー情報を返却",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = LoginResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（IDトークンが無効など）"),
			@ApiResponse(responseCode = "401", description = "認証失敗")
	})
	@PostMapping("/google")
	public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody @Valid LoginRequest request) {
		LoginResponse response = authService.loginWithGoogle(request.getIdToken());

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "ゲストユーザーとしてログインする", description = "ゲストユーザーとして一時的にログインし、JWTトークンを取得します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ゲストログイン成功、JWTトークンを返却",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = AuthResponse.class)))
	})
	@PostMapping("/guest")
	public ResponseEntity<AuthResponse> guestLogin() {
		String guestId = "guest-user";
		String guestRole = "ROLE_GUEST";

		String token = jwtTokenProvider.generateToken(guestId, guestRole);

		return ResponseEntity.ok(AuthResponse.builder().token(token).build());
	}

	/**
	 * コールドスタート防止のためのウォームアップエンドポイント
	 */
	@Operation(summary = "サーバーのウォームアップ", description = "サーバーのコールドスタートを防ぐための簡易的なヘルスチェックエンドポイントです。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(mediaType = "text/plain",
							schema = @Schema(type = "string", example = "OK")))
	})
	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("OK");
	}
}