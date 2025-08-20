package com.rikuto.revox.repository;

import com.rikuto.revox.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ユーザーに関するリポジトリです。
 * uniqueUserIdを主キーとして使用します。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	/**
	 * ユーザーIDでの検索を行います。論理削除された情報は取得しません。
	 * ログイン後の内部操作で使用します。。
	 *
	 * @param id 　ユーザーID
	 * @return ユーザー情報
	 */
	Optional<User> findByIdAndIsDeletedFalse(Integer id);

	/**
	 * アプリケーション独自の一意なユーザーIDでの検索を行います。
	 * 論理削除された情報は取得しません。
	 * JWTフィルターでの認証に使用します。
	 *
	 * @param uniqueUserId アプリケーション独自の一意なユーザーID
	 * @return アプリケーション独自の一意なユーザーIDに紐づいたユーザー情報
	 */
	Optional<User> findByUniqueUserIdAndIsDeletedFalse(String uniqueUserId);

	/**
	 * アプリケーション独自の一意なユーザーIDで論理削除されたユーザー含む全件検索を行います。
	 * 論理削除されたユーザーを復元するために使用します。
	 *
	 * @param uniqueUserId アプリケーション独自の一意なユーザーID
	 * @return DBに存在する全てのユーザー情報
	 */
	Optional<User> findByUniqueUserId(String uniqueUserId);
}
