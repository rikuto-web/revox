package com.rikuto.revox.service;

import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.MaintenanceTask;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.MaintenanceTaskMapper;
import com.rikuto.revox.repository.CategoryRepository;
import com.rikuto.revox.repository.MaintenanceTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 整備タスクに関するビジネスロジックを処理するサービスクラスです。
 */
@Service
public class MaintenanceTaskService {

	private final CategoryRepository categoryRepository;
	private final MaintenanceTaskRepository maintenanceTaskRepository;

	private final MaintenanceTaskMapper maintenanceTaskMapper;

	public MaintenanceTaskService(CategoryRepository categoryRepository,
	                              MaintenanceTaskRepository maintenanceTaskRepository,
	                              MaintenanceTaskMapper maintenanceTaskMapper) {
		this.categoryRepository = categoryRepository;
		this.maintenanceTaskRepository = maintenanceTaskRepository;
		this.maintenanceTaskMapper = maintenanceTaskMapper;
	}

	/**
	 * カテゴリーIDに紐づいた整備タスクの検索機能です。
	 *
	 * @param categoryId カテゴリーID
	 * @return レスポンスへ返還後の整備タスク
	 */
	public List<MaintenanceTaskResponse> findMaintenanceTaskByCategoryId(Integer categoryId) {
		List<MaintenanceTask> maintenanceTask = maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(categoryId);

		return maintenanceTaskMapper.toResponseList(maintenanceTask);
	}

	/**
	 * カテゴリーIDに紐づけて整備タスクを新規登録します。
	 *
	 * @param request 登録する整備タスクのリクエスト情報
	 * @return 登録後の整備タスク情報
	 */
	@Transactional
	public MaintenanceTaskResponse registerMaintenanceTask(MaintenanceTaskRequest request) {
		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"カテゴリーID " + request.getCategoryId() + " が見つかりません。"));

		MaintenanceTask maintenanceTask = maintenanceTaskMapper.toEntity(request, category);

		MaintenanceTask savedTask = maintenanceTaskRepository.save(maintenanceTask);

		return maintenanceTaskMapper.toResponse(savedTask);
	}

	/**
	 * 整備タスクの更新を行います。
	 *
	 * @param maintenanceTaskId 整備タスクID
	 * @param updateMaintenance 更新するリクエスト情報
	 * @return 更新後の整備タスク情報
	 */
	@Transactional
	public MaintenanceTaskResponse updateMaintenanceTask(Integer maintenanceTaskId ,
	                                                     MaintenanceTaskRequest updateMaintenance) {
		MaintenanceTask existingMaintenanceTask = maintenanceTaskRepository.findById(maintenanceTaskId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"整備タスクID " + maintenanceTaskId + " が見つかりません。"));

		existingMaintenanceTask.updateFrom(updateMaintenance);

		maintenanceTaskRepository.save(existingMaintenanceTask);

		return maintenanceTaskMapper.toResponse(existingMaintenanceTask);
	}

	/**
	 * 整備タスクの論理削除を行います。
	 *
	 * @param maintenanceTaskId 整備タスクID
	 */
	@Transactional
	public void softDeleteMaintenanceTask(Integer maintenanceTaskId) {
		MaintenanceTask existingMaintenanceTask = maintenanceTaskRepository.findById(maintenanceTaskId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"整備タスクID " + maintenanceTaskId + " が見つかりません。"));

		existingMaintenanceTask.softDelete();

		maintenanceTaskRepository.save(existingMaintenanceTask);
	}
}
