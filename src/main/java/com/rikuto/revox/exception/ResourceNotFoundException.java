package com.rikuto.revox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * リソースが見つからなかった場合にスローされるカスタム例外です。
 * この例外がスローされると、HTTP 404 Not Found ステータスが返されます。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

	/**
	 * 指定された詳細メッセージを持つ新しい ResourceNotFoundException を構築します。
	 * @param message 例外の詳細メッセージ
	 */
	public ResourceNotFoundException(String message) {
		super(message);
	}

	/**
	 * 指定された詳細メッセージと原因を持つ新しい ResourceNotFoundException を構築します。
	 * @param message 例外の詳細メッセージ
	 * @param cause この例外の原因となるThrowable (null可)
	 */
	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}