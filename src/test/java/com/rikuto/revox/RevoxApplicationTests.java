package com.rikuto.revox;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.rikuto.revox.security.jwt.JwtTokenProvider;
import com.rikuto.revox.service.GeminiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class RevoxApplicationTests {

	@MockitoBean
	private GeminiService geminiService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private GoogleIdTokenVerifier googleIdTokenVerifier;

	@Test
	void contextLoads() {
	}

}
