package com.rikuto.revox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ユーザーが既に存在する場合にスローされるカスタム例外です。
 * この例外がスローされると、HTTP 409 Conflict ステータスが返されます。
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {

	/**
	 * 指定された詳細メッセージを持つ新しい UserAlreadyExistsException を構築します。
	 * @param message 例外の詳細メッセージ
	 */
	public UserAlreadyExistsException(String message) {
		super(message);
	}

	/**
	 * 指定された詳細メッセージと原因を持つ新しい UserAlreadyExistsException を構築します。
	 * @param message 例外の詳細メッセージ
	 * @param cause この例外の原因となるThrowable (null可)
	 */
	public UserAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}