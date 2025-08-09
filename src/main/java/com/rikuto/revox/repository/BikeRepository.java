package com.rikuto.revox.repository;

import com.rikuto.revox.domain.bike.Bike;
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
	 *
	 * @param userId 一意のユーザID
	 * @return ユーザーに紐づいたバイク情報リスト
	 */
	List<Bike> findByUserIdAndIsDeletedFalse(Integer userId);

	/**
	 * ユーザーに紐づいた特定のバイクを検索します。
	 * 該当するバイクがない場合は、Optional.empty()を返します。
	 *
	 * @param userId ユーザーID
	 * @param bikeId 一意のバイクID
	 * @return ユーザーに紐づいた単一のバイク情報（Optionalでラップ）
	 */
	Optional<Bike> findByIdAndUserIdAndIsDeletedFalse(Integer userId, Integer bikeId);
}
