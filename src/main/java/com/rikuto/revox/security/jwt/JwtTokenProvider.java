package com.rikuto.revox.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWTトークンの生成と検証を行うクラスです。
 */
@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final Long validityInMilliseconds;

	/**
	 * 秘密キーおよび有効期限に関するコンストラクタです。
	 * 秘密キーはHS256アルゴリズムに適合するバイト型に変換されます。
	 *
	 * @param secretKey 秘密キー
	 * @param validityInMilliseconds 有効期限
	 */
	public JwtTokenProvider(@Value("${JWT_SECRET_KEY}") String secretKey,
	                        @Value("${JWT_EXPIRATION}") Long validityInMilliseconds) {

		this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
		this.validityInMilliseconds = validityInMilliseconds;
	}

	/**
	 * 認証成功時にJWTトークンの生成を行います。
	 * @param uniqueUserId ユーザーの一意なID
	 * @return 生成されたJWT文字列
	 */
	public String generateToken(String uniqueUserId){
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()
				.setSubject(uniqueUserId)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(secretKey, SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * 生成後のJWTトークンからIDの抽出を行います。
	 * @param token 生成済みのJWTトークン
	 * @return ユーザーの一意なID
	 */
	public String getUniqueUserIdFromToken(String token){
		return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()

				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	/**
	 * 受け取ったトークンが有効か検証します。
	 * @param token 生成済みのJWTトークン
	 * @return トークンの有効判定
	 */
	public boolean validateToken(String token){
		try {
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

			return true;
		}catch(Exception e){
			return false;
		}
	}
}
