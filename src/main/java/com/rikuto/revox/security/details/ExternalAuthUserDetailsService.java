package com.rikuto.revox.security.details;

import com.rikuto.revox.domain.User;
import com.rikuto.revox.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 外部認証専用のUserDetailsServiceです。
 * uniqueUserIdを使用してユーザー情報を取得します。
 */
@Service
public class ExternalAuthUserDetailsService {

	private final UserRepository userRepository;

	public ExternalAuthUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * uniqueUserIdでユーザー情報を検索し、UserDetailsを返します。
	 *
	 * @param uniqueUserId 外部認証システムの一意なユーザーID
	 * @return UserDetails実装オブジェクト
	 * @throws UsernameNotFoundException ユーザーが見つからない場合
	 */
	public UserDetails loadUserByUniqueUserId(String uniqueUserId) throws UsernameNotFoundException {
		User uniqueUser = userRepository.findByUniqueUserIdAndIsDeletedFalse(uniqueUserId)
				.orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + uniqueUserId));

		return new ExternalAuthUserDetails(uniqueUser);
	}
}