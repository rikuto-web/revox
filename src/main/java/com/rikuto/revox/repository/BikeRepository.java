package com.rikuto.revox.repository;

import com.rikuto.revox.domain.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * バイクに関するリポジトリです。
 * JpaRepositoryを継承しています。
 */
@Repository
public interface BikeRepository extends JpaRepository<Bike, Integer> {

	/**
	 * ユーザIDに紐づいた全てのバイク情報の検索を行います。
	 * @param userId 一意のユーザID
	 * @return ユーザーに紐づいたバイク情報リスト
	 */
	Optional<Bike> findByUserIdAndIsDeletedFalse(Integer userId);

	/**
	 * ユーザーに紐づいた特定のバイクを検索します。
	 *
	 * @param bikeId 一意のバイクID
	 * @param userId ユーザーID
	 * @return ユーザーに紐づいた単一のバイク情報
	 */
	Optional<Bike> findByIdAndUserIdAndIsDeletedFalse(Integer userId, Integer bikeId);
}
