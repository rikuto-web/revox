package com.rikuto.revox.service;

import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.domain.user.UserUpdateDate;
import com.rikuto.revox.dto.user.UserUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.UserResponseMapper;
import com.rikuto.revox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ﾕｰｻﾞｰに関するビジネスロジックを処理するサービスクラスです。
 */
@Service
public class UserService {

	private final UserRepository userRepository;

	private final UserResponseMapper userResponseMapper;

	public UserService(UserRepository userRepository,
	                   UserResponseMapper userResponseMapper) {
		this.userRepository = userRepository;
		this.userResponseMapper = userResponseMapper;
	}

	/**
	 * ユーザーIDでユーザー情報を検索し、フロント側でユーザー情報を取得します。
	 *
	 * @param userId 一意のユーザーID
	 * @return ユーザー情報
	 */
	public User findById(Integer userId) {
		return userRepository.findByIdAndIsDeletedFalse(userId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));
	}

	/**
	 * ユーザー情報の更新を行います。
	 * 外部認証のためニックネームのみ更新可能です。
	 *
	 * @param updateRequest 更新リクエスト
	 * @param userId 一意のユーザーID
	 * @return 更新後のユーザーレスポンス
	 */
	@Transactional
	public UserResponse updateUser(UserUpdateRequest updateRequest, Integer userId) {
		User existingUser = userRepository.findByIdAndIsDeletedFalse(userId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

		UserUpdateDate updateUser = UserUpdateDate.builder()
				.nickname(updateRequest.getNickname())
				.build();

		existingUser.updateFrom(updateUser);

		User savedUser = userRepository.save(existingUser);

		return userResponseMapper.toResponse(savedUser);
	}

	/**
	 * ユーザー情報を論理削除します。
	 *
	 * @param userId 一意のユーザーID
	 */
	@Transactional
	public void softDeleteUser(Integer userId) {
		User existingUser = userRepository.findByIdAndIsDeletedFalse(userId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

		existingUser.softDelete();

		userRepository.save(existingUser);
	}

	/**
	 * 外部認証でのユーザー検索または新規登録を行います。
	 *
	 * @param uniqueUserId 外部認証での各一意のID（Googleのsubクレームなど）
	 * @param name 外部認証先のユーザーネーム
	 * @param email 外部認証に使用されているメールアドレス
	 * @return ユーザー情報
	 */
	@Transactional
	public User findOrCreateUser(String uniqueUserId, String name, String email) {
		return userRepository.findByUniqueUserIdAndIsDeletedFalse(uniqueUserId)
				.orElseGet(() -> {
					User newUser = User.builder()
							.uniqueUserId(uniqueUserId)
							.nickname(name)
							.displayEmail(email)
							.build();
					return userRepository.save(newUser);
				});
	}
}