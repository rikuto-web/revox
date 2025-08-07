package com.rikuto.revox.security;

import com.rikuto.revox.security.details.ExternalAuthUserDetailsService;
import com.rikuto.revox.security.jwt.JwtAuthenticationFilter;
import com.rikuto.revox.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security設定に関するBeanを設置したクラスです。
 * 全てのリクセストはJwtAuthenticationFilterを通過します。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final ExternalAuthUserDetailsService userDetailsService;

	public SecurityConfig(JwtTokenProvider jwtTokenProvider,
	                      ExternalAuthUserDetailsService userDetailsService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * JWTおよびユーザー詳細をSpringが認識するようBeanコンテナにするメソッド
	 *
	 * @return JWTおよびユーザー詳細
	 */
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
	}

	/**
	 * セキュリティフィルターチェーンをカスタマイズしてBeanとして登録します。
	 * JWT認証とステートレスなセッション管理を設定します。
	 *
	 * @param http Spring Securityの設定ハブ
	 * @return カスタマイズされたSecurityFilterChainオブジェクト
	 * @throws Exception 設定時に発生する可能性のある例外
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/api/auth/**").permitAll()
						.anyRequest().authenticated()
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				);

		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}