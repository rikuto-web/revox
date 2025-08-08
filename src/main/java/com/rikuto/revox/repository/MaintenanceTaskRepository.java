package com.rikuto.revox.repository;

import com.rikuto.revox.domain.maintenancetask.MaintenanceTask;
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
	 * カテゴリーIDに紐づいた整備タスクの検索を行います
	 *
	 * @param categoryId カテゴリーID
	 * @return カテゴリーIDに紐づいた整備タスクリスト
	 */
	List<MaintenanceTask> findByCategoryIdAndIsDeletedFalse(Integer categoryId);

	/**
	 *
	 * @param categoryId
	 * @param MaintenanceTaskId
	 * @return
	 */
	Optional<MaintenanceTask> findByCategoryIdAndIdAndIsDeletedFalse(Integer categoryId, Integer MaintenanceTaskId);
}
