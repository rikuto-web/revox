package com.rikuto.revox.security.jwt;

import com.rikuto.revox.security.details.ExternalAuthUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT認証フィルターです。
 * JWTトークンからuniqueUserIdを抽出し、認証情報を設定します。
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final ExternalAuthUserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
	                               ExternalAuthUserDetailsService userDetailsService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * 各HTTPリクエストに対して一度だけ実行されるフィルター処理です。
	 * このメソッドは、以下のステップでJWT認証を行います。
	 * 1. リクエストヘッダーからJWTトークンを抽出し、その有効性を検証します。
	 * 2. 有効なトークンからユーザー情報を取得し、認証オブジェクトを生成します。
	 * 3. 認証オブジェクトをSecurityContextに設定し、後続の処理でユーザーが認証済みと認識されるようにします。
	 * トークンが無効な場合や存在しない場合は、認証は行われず、次のフィルターへ処理が渡されます。
	 *
	 * @param request     HTTPリクエスト
	 * @param response    HTTPレスポンス
	 * @param filterChain フィルターチェーン
	 * @throws ServletException Servlet例外
	 * @throws IOException      入出力例外
	 */
	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request,
	                                @NotNull HttpServletResponse response,
	                                @NotNull FilterChain filterChain) throws ServletException, IOException {
		try {
			log.info("JWTの検証を開始します。");
			String jwt = getJwtFromRequest(request);
			if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

				String uniqueUserId = jwtTokenProvider.getUniqueUserIdFromToken(jwt);
				UserDetails userDetails = userDetailsService.loadUserByUniqueUserId(uniqueUserId);

				UsernamePasswordAuthenticationToken authentication
						= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
				log.info("ユーザーが正常に認証されました。");
			} else {
				log.debug("JWTトークンが見つかりませんでした。");
			}
		} catch(Exception ex) {
			log.error("セキュリティコンテキストにユーザー認証を設定できませんでした。");
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * リクエストヘッダーからJWTトークンを抽出します。
	 *
	 * @param request HTTPリクエスト
	 * @return JWTトークン文字列（Bearerプレフィックスを除く）
	 */
	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {

			return bearerToken.substring(7);
		}
		return null;
	}
}