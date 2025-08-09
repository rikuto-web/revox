package com.rikuto.revox.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全てのコントローラーで発生する例外をグローバルに処理するハンドラーです。

 * アプリケーション全体で発生する特定の例外（例：リソースが見つからない、認証エラー、バリデーションエラー）
 * を捕捉し、適切なHTTPステータスコードとエラーメッセージを含むレスポンスを返します。
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * リソースが見つからない場合に発生するResourceNotFoundExceptionを処理します。
	 * クライアントにはHTTP 404 Not Foundステータスコードを返します。
	 * * @param ex 発生したResourceNotFoundException
	 * @return エラーメッセージを含むResponseEntity
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	/**
	 * 認証に失敗した場合に発生するAuthenticationExceptionを処理します。
	 * クライアントにはHTTP 401 Unauthorizedステータスコードを返します。
	 * * @param ex 発生したAuthenticationException
	 * @return エラーメッセージを含むResponseEntity
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	/**
	 * ValidアノテーションによるバリデーションエラーであるMethodArgumentNotValidExceptionを処理します。
	 * 無効なリクエストボディが送信された場合に発生し、すべてのバリデーションエラーメッセージをリスト形式で返します。
	 * クライアントにはHTTP 400 Bad Requestステータスコードを返します。
	 * * @param ex 発生したMethodArgumentNotValidException
	 * @return エラーメッセージのリストを含むResponseEntity
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		List<String> errors = ex.getBindingResult()
				.getAllErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.toList());

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	/**
	 * パス変数やリクエストパラメータのバリデーションエラーであるHandlerMethodValidationExceptionを処理します。
	 * 無効なパス変数などが送信された場合に発生し、すべてのバリデーションエラーメッセージをリスト形式で返します。
	 * クライアントにはHTTP 400 Bad Requestステータスコードを返します。
	 * @param ex 発生したHandlerMethodValidationException
	 * @return エラーメッセージのリストを含むResponseEntity
	 */
	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<List<String>> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
		List<String> errors = ex.getAllErrors().stream()
				.map(MessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.toList());

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}


	/**
	 * 上記で捕捉されなかった予期せぬすべての例外を処理します。
	 * アプリケーションの内部エラーをクライアントに直接詳細に開示しないため、汎用的なエラーメッセージを返します。
	 * クライアントにはHTTP 500 Internal Server Errorステータスコードを返します。
	 * * @param ex 発生した例外
	 * @return 汎用的なエラーメッセージを含むResponseEntity
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleAllUncaughtException(Exception ex) {
		log.error("内部サーバーエラーが発生しました", ex);
		return new ResponseEntity<>("内部サーバーエラーが発生しました。", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}