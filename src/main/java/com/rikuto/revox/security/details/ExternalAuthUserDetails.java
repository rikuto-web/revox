package com.rikuto.revox.security.details;

import com.rikuto.revox.domain.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * 外部認証専用のUserDetailsです。
 * uniqueUserIdを主キーとして使用し、パスワード認証は行いません。
 * セキュリティ設定（有効期限、ロック機能、非アクティブ）は全てtureのため無効です。
 */
@EqualsAndHashCode
public class ExternalAuthUserDetails implements UserDetails {

	@Getter
	private final int id;
	@Getter
	private final String uniqueUserId;

	private final Collection<? extends GrantedAuthority> authorities;

	/**
	 * AuthUserドメインからUserDetailsを作成するコンストラクタです。
	 *
	 * @param user 認証済みユーザーのドメインオブジェクト
	 */
	public ExternalAuthUserDetails(User user) {

		this.id = user.getId();
		this.uniqueUserId = user.getUniqueUserId();

		// カンマ区切りでロールを分け、文字列として役割を取得します。
		this.authorities = user.getRoles() != null ?
				Arrays.stream(user.getRoles().split(","))
						.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
						.toList() :
				Collections.emptyList();
	}


	@Override
	public String getUsername() {
		return uniqueUserId; // 外部認証ではuniqueUserIdが識別子
	}

	@Override
	public String getPassword() {
		return null; // 外部認証のためパスワードは不要
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}