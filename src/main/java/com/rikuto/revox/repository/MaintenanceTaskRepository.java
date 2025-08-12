package com.rikuto.revox.repository;

import com.rikuto.revox.domain.maintenancetask.MaintenanceTask;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 整備タスクに関するリポジトリです。
 * JpaRepositoryを継承しています。
 */
@Repository
public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Integer> {

	/**
	 * 指定されたユーザーIDに紐づく、論理削除されていない最新の整備タスクを指定件数分検索します。
	 *
	 * @param userId ユーザーID
	 * @param pageable ページング情報
	 * @return 整備タスクList
	 */
	List<MaintenanceTask> findByBike_UserIdAndIsDeletedFalse(Integer userId, Pageable pageable);

	/**
	 * 指定されたバイクIDに紐づく、論理削除されていないすべての整備タスクを検索します。
	 *
	 * @param bikeId バイクID
	 * @return 整備タスクList
	 */
	List<MaintenanceTask> findByBikeIdAndIsDeletedFalse(Integer bikeId);

	/**
	 * 指定されたバイクIDとカテゴリーIDに紐づく、論理削除されていないすべての整備タスクを検索します。
	 *
	 * @param bikeId バイクID
	 * @param categoryId カテゴリーID
	 * @return 整備タスクList
	 */
	List<MaintenanceTask> findByBikeIdAndCategoryIdAndIsDeletedFalse(Integer bikeId, Integer categoryId);

	/**
	 * カテゴリーID、整備タスクIDに紐づく、論理削除されていない整備タスクを検索します。
	 *
	 * @param categoryId カテゴリーのID
	 * @param id 整備タスクID
	 * @return 整備タスクをOptionalで返します。
	 */
	Optional<MaintenanceTask> findByCategoryIdAndIdAndIsDeletedFalse(Integer categoryId, Integer id);

}
