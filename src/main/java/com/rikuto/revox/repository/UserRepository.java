package com.rikuto.revox.repository;

import com.rikuto.revox.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ユーザーに関するリポジトリです。
 * JpaRepositoryを継承しています。
 * 管理者機能として論理削除を考慮しないCRUDに関してはJPAの標準機能を利用します。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	/**
	 * 登録後のユーザーIDでの検索を行います。論理削除された情報は取得しません。
	 *
	 * @param id　ユーザーID
	 * @return ユーザー情報
	 */
	Optional<User> findByIdAndIsDeletedFalse(Integer id);

	/**
	 * メールアドレスでの検索を行います。論理削除された情報は取得しません。
	 *
	 * @param email メールアドレス
	 * @return メールアドレスに紐づいたユーザー情報
	 */
	Optional<User> findByEmailAndIsDeletedFalse(String email);

	/**
	 * グーグルIDでの検索を行います。論理削除された情報は取得しません。
	 *
	 * @param googleId グーグルID
	 * @return グーグルIDに紐づいたユーザー情報
	 */
	Optional<User> findByGoogleIdAndIsDeletedFalse(String googleId);

	/**
	 * LineIDでの検索を行います。論理削除された情報は取得しません。
	 *
	 * @param lineId ラインID
	 * @return ラインIDに紐づいたユーザー情報
	 */
	Optional<User> findByLineIdAndIsDeletedFalse(String lineId);

}
