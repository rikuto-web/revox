package com.rikuto.revox.exception; // または com.rikuto.revox.advice

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // バリデーションエラー用
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.context.support.DefaultMessageSourceResolvable; // エラーメッセージ取得用
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * ResourceNotFoundException を処理します。
	 * リソースが見つからない場合に HTTP 404 Not Found を返します。
	 * @param ex 発生した ResourceNotFoundException
	 * @return エラーメッセージと HTTP 404 ステータス
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	/**
	 * @Valid によるバリデーションエラー (MethodArgumentNotValidException) を処理します。
	 * 無効なリクエストボディが送信された場合に HTTP 400 Bad Request を返します。
	 * @param ex 発生した MethodArgumentNotValidException
	 * @return エラーメッセージのリストと HTTP 400 ステータス
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
	 * その他の予期せぬ例外を処理します。
	 * アプリケーションで捕捉されなかったすべての例外に対して HTTP 500 Internal Server Error を返します。
	 * @param ex 発生した例外
	 * @return エラーメッセージと HTTP 500 ステータス
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleAllUncaughtException(Exception ex) {
		// 本番環境では、詳細なエラーメッセージをクライアントに直接返さず、ログに記録し、
		// 汎用的なエラーメッセージを返すことを検討してください。
		ex.printStackTrace(); // デバッグ目的でスタックトレースを出力
		return new ResponseEntity<>("内部サーバーエラーが発生しました。", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}