package com.rikuto.revox.dto.auth;

import lombok.Builder;
import lombok.Getter;

/**
 * Google IDトークンのペイロード情報を保持するDTOです。
 */
@Getter
@Builder
public class GoogleTokenPayload {

	private final String sub;
	private final String email;
	private final String name;
}