package com.rikuto.revox.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Google IDトークンのペイロード情報を保持するDTOです。
 */
@Getter
@Builder
@Schema(description = "Google IDトークンのペイロード情報を表すDTOです。")
public class GoogleTokenPayload {

	@Schema(description = "Googleから取得したユーザーの一意なID。アプリケーション内でユーザーを一意に識別するために使用されます。")
	private final String sub;

	@Schema(description = "Googleアカウントに関連付けられたメールアドレス。")
	private final String email;

	@Schema(description = "Googleアカウントに登録されたユーザー名。")
	private final String name;
}