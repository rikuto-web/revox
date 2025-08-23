package com.rikuto.revox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikuto.revox.dto.auth.LoginRequest;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.security.jwt.JwtTokenProvider;
import com.rikuto.revox.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
		controllers = AuthUserController.class,
		excludeAutoConfiguration = {
				SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class
		}
)
class AuthUserControllerTest {

	private final String dummyAccessToken = "dummyAccessToken";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	private LoginRequest LoginRequest;
	private LoginResponse loginResponse;

	@BeforeEach
	void setup() {
		String dummyIdToken = "dummyIdToken";
		LoginRequest = new LoginRequest(dummyIdToken);

		loginResponse = LoginResponse.builder()
				.accessToken(dummyAccessToken)
				.tokenType("Bearer")
				.user(UserResponse.builder()
						.id(1)
						.nickname("testUser")
						.uniqueUserId("unique_user_id")
						.createdAt(LocalDateTime.now())
						.build())
				.build();
	}

	@Test
	public void Googleの外部認証で正常にログインできること() throws Exception {
		when(authService.loginWithGoogle(LoginRequest.getIdToken())).thenReturn(loginResponse);

		mockMvc.perform(post("/api/auth/google")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(LoginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").value(dummyAccessToken))
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.user.id").value(1))
				.andExpect(jsonPath("$.user.nickname").value("testUser"));

		verify(authService).loginWithGoogle(LoginRequest.getIdToken());
	}

	@Test
	void 不正なリクエストの場合400_Bad_Requestを返すこと() throws Exception {
		LoginRequest invalidRequest = new LoginRequest("");

		mockMvc.perform(post("/api/auth/google")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());

		verify(authService, Mockito.never()).loginWithGoogle(Mockito.anyString());
	}
}