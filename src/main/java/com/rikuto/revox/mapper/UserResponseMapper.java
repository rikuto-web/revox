package com.rikuto.revox.mapper;

import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.dto.user.UserResponse;
import org.springframework.stereotype.Component;

/**
 * Userドメインと関連するDTO間のマッピングを行うクラスです。
 */
@Component
public class UserResponseMapper {

	/**
	 * UserドメインをUserResponseに変換します。
	 *
	 * @param user ユーザー情報
	 * @return 変換後のレスポンスユーザー情報
	 */
	public UserResponse toResponse(User user) {

		return UserResponse.builder()
				.id(user.getId())
				.uniqueUserId(user.getUniqueUserId())

				.nickname(user.getNickname())
				.displayEmail(user.getDisplayEmail())

				.createdAt(user.getCreatedAt())
				.build();
	}
}