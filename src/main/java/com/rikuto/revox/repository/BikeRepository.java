package com.rikuto.revox.repository;

import com.rikuto.revox.entity.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * バイクに関するリポジトリです。
 * JpaRepositoryを継承しています。
 */
@Repository
public interface BikeRepository extends JpaRepository<Bike, Integer> {

	/**
	 * ユーザIDに紐づいたバイク情報の検索を行います。
	 * @param userId 一意のユーザID
	 * @return ユーザーに紐づいたバイク情報リスト
	 */
	List<Bike> findByUserId(Integer userId);

}
