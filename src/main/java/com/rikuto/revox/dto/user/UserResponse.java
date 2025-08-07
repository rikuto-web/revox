package com.rikuto.revox.dto.user;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * ログイン後に公開するユーザー情報のレスポンスDTOです。
 */
@Getter
@Builder
public class UserResponse {
	private final int id;
	private final String nickname;
	private final String displayEmail;
	private final String uniqueUserId;
	private final LocalDateTime createdAt;
}