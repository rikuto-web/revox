package com.rikuto.revox.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ログイン後に公開するユーザー情報のレスポンスDTOです。
 */
@Getter
@Builder
@Schema(description = "ログイン後に返されるユーザー情報のレスポンスDTOです。")
public class UserResponse {

	@Schema(description = "ユーザーの一意なID。")
	private final int id;

	@Schema(description = "外部認証システムから取得した一意なユーザーID。")
	private final String uniqueUserId;

	@Schema(description = "ユーザーのニックネーム。")
	private final String nickname;

	@Schema(description = "外部認証から取得した表示用のメールアドレス。")
	private final String displayEmail;

	@Schema(description = "ユーザー情報が作成された日時。")
	private LocalDateTime createdAt;

	@Schema(description = "ユーザー情報が最後に更新された日時。")
	private LocalDateTime updatedAt;
}