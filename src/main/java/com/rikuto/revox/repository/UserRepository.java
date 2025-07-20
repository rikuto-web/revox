package com.rikuto.revox.repository;

import com.rikuto.revox.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザーに関するリポジトリです。
 * JpaRepositoryを継承しています。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	/**
	 * メールアドレスでの検索を行います。
	 * @param email メールアドレス
	 * @return メールアドレスに紐づいたユーザー情報
	 */
	User findByEmail(String email);

	/**
	 * グーグルIDでの検索を行います。
	 * @param googleId グーグルID
	 * @return グーグルIDに紐づいたユーザー情報
	 */
	User findByGoogleId(String googleId);

	/**
	 * LineIDでの検索を行います。
	 * @param lineId ラインID
	 * @return ラインIDに紐づいたユーザー情報
	 */
	User findByLineId(String lineId);

}
