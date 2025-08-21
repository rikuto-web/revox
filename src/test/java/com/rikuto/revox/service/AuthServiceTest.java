package com.rikuto.revox.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.exception.AuthenticationException;
import com.rikuto.revox.mapper.LoginResponseMapper;
import com.rikuto.revox.mapper.UserResponseMapper;
import com.rikuto.revox.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	private final String dummyIdToken = "dummy_google_id_token";
	@Mock
	private UserService userService;
	@Mock
	private UserResponseMapper userResponseMapper;
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	@Mock
	private GoogleIdTokenVerifier googleIdTokenVerifier;
	@Mock
	private LoginResponseMapper loginResponseMapper;
	@InjectMocks
	private AuthService authService;
	private User testUser;
	private UserResponse userResponse;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.id(9999)
				.uniqueUserId("google-sub-id")
				.nickname("Test User")
				.displayEmail("test@google.com")
				.build();

		userResponse = UserResponse.builder()
				.id(9999)
				.nickname("Test User")
				.displayEmail("test@google.com")
				.build();
	}

	@Test
	void 無効なIDトークンでログインした場合AuthenticationExceptionをスロー() throws GeneralSecurityException, IOException {
		when(googleIdTokenVerifier.verify(anyString()))
				.thenThrow(new GeneralSecurityException("無効なGoogle IDトークンです。"));

		assertThatThrownBy(() -> authService.loginWithGoogle(dummyIdToken))
				.isInstanceOf(AuthenticationException.class)
				.hasMessage("Google IDトークンの検証に失敗しました。");

		verify(userService, never()).findOrCreateUser(any(), any(), any());
		verify(jwtTokenProvider, never()).generateToken(any());
		verify(userResponseMapper, never()).toResponse(any());
	}
}