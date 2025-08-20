package com.rikuto.revox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 認証失敗時にスローされるカスタム例外です。
 * この例外がスローされると、HTTP 401 Unauthorized ステータスが返されます。
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {

	/**
	 * 指定された詳細メッセージを持つ新しい AuthenticationException を構築します。
	 *
	 * @param message 例外の詳細メッセージ
	 */
	public AuthenticationException(String message) {
		super(message);
	}

	/**
	 * 指定された詳細メッセージと原因を持つ新しい AuthenticationException を構築します。
	 *
	 * @param message 例外の詳細メッセージ
	 * @param cause   この例外の原因となるThrowable (null可)
	 */
	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}