package com.rikuto.revox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikuto.revox.dto.auth.LoginRequest;
import com.rikuto.revox.dto.auth.LoginResponse;
import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.reset;
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
@Import(AuthUserControllerTest.AuthUserControllerTestConfig.class)
class AuthUserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AuthService authService;

	private LoginRequest LoginRequest;
	private LoginResponse loginResponse;

	private final String dummyAccessToken = "dummyAccessToken";

	@BeforeEach
	void setup() {
		String dummyIdToken = "dummyIdToken";
		LoginRequest = new LoginRequest(dummyIdToken);

		loginResponse = LoginResponse.builder()
				.accessToken(dummyAccessToken)
				.tokenType("Bearer")
				.user(UserResponse.builder()
						.id(999999)
						.nickname("testUser")
						.uniqueUserId("unique_user_id")
						.displayEmail("test@example.com")
						.createdAt(LocalDateTime.now())
						.build())
				.build();
		reset(authService);
	}


	@Test
	public void Googleの外部認証で正常にログインできること()throws Exception{
		when(authService.loginWithGoogle(LoginRequest.idToken())).thenReturn(loginResponse);

		mockMvc.perform(post("/api/auth/google")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(LoginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").value(dummyAccessToken))
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.user.id").value(999999))
				.andExpect(jsonPath("$.user.nickname").value("testUser"))
				.andExpect(jsonPath("$.user.displayEmail").value("test@example.com"));
	}

	@Test
	void 不正なリクエストの場合400_Bad_Requestを返すこと() throws Exception {
		LoginRequest invalidRequest = new LoginRequest("");

		mockMvc.perform(post("/api/auth/google")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());

		Mockito.verify(authService, Mockito.never()).loginWithGoogle(Mockito.anyString());
	}

	@TestConfiguration
	static class AuthUserControllerTestConfig {
		@Bean
		public AuthService authService() {
			return Mockito.mock(AuthService.class);
		}
	}
}