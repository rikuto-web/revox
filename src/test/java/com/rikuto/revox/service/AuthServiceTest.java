package com.rikuto.revox.service;

import com.rikuto.revox.domain.User;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.exception.AuthenticationException;
import com.rikuto.revox.mapper.UserResponseMapper;
import com.rikuto.revox.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserService userService;

	@Mock
	private UserResponseMapper userResponseMapper;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	private AuthService authService;

	private User testUser;
	private UserResponse userResponse;

	private final String dummyIdToken = "dummy_google_id_token";
	private final String dummyAccessToken = "dummy_access_token";

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.id(1)
				.uniqueUserId("google-sub-id")
				.nickname("Test User")
				.displayEmail("test@google.com")
				.build();

		userResponse = UserResponse.builder()
				.id(1)
				.nickname("Test User")
				.displayEmail("test@google.com")
				.build();
	}

	@Test
	void Googleの外部認証で正常にログインできること() {
		when(userService.findOrCreateUser(any(), any(), any())).thenReturn(testUser);
		when(jwtTokenProvider.generateToken(testUser.getUniqueUserId())).thenReturn(dummyAccessToken);
		when(userResponseMapper.toResponse(testUser)).thenReturn(userResponse);

		LoginResponse result = authService.loginWithGoogle(dummyIdToken);

		assertThat(result.getAccessToken()).isEqualTo(dummyAccessToken);
		assertThat(result.getTokenType()).isEqualTo("Bearer");
		assertThat(result.getUser()).isEqualTo(userResponse);
		verify(userService).findOrCreateUser(any(), any(), any());
		verify(jwtTokenProvider).generateToken(testUser.getUniqueUserId());
		verify(userResponseMapper).toResponse(testUser);
	}

	@Test
	void 無効なIDトークンでログインした場合AuthenticationExceptionをスロー() {
		assertThatThrownBy(() -> authService.loginWithGoogle(dummyIdToken))
				.isInstanceOf(AuthenticationException.class);

		verify(userService, never()).findOrCreateUser(any(), any(), any());
		verify(jwtTokenProvider, never()).generateToken(any());
		verify(userResponseMapper, never()).toResponse(any());
	}
}