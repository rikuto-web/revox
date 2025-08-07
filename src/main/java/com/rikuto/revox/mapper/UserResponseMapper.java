package com.rikuto.revox.mapper;

import com.rikuto.revox.domain.User;
import com.rikuto.revox.dto.user.UserResponse;
import org.springframework.stereotype.Component;

/**
 * 外部認証専用のUserMapperです。
 */
@Component
public class UserResponseMapper {

	/**
	 * AuthUserドメインをUserResponseに変換します。
	 * 認証後のユーザー情報をフロントへ渡すためのレスポンスマッパーです。
	 *
	 * @param user ユーザー情報
	 * @return 返還後のレスポンスユーザー情報
	 */
	public UserResponse toResponse(User user) {
		return UserResponse.builder()
				.id(user.getId())
				.nickname(user.getNickname())
				.displayEmail(user.getDisplayEmail())
				.uniqueUserId(user.getUniqueUserId())
				.createdAt(user.getCreatedAt())
				.build();
	}
}