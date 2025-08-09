package com.rikuto.revox.service;

import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.maintenancetask.MaintenanceTask;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskUpdateRequest;
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
	 * カテゴリーIDに紐づいた全ての整備タスクの検索機能です。
	 *
	 * @param categoryId カテゴリーID
	 * @return レスポンスへ返還後の整備タスク
	 */
	public List<MaintenanceTaskResponse> findMaintenanceTaskByCategoryId(Integer categoryId) {
		List<MaintenanceTask> maintenanceTaskList = maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(categoryId);

		return maintenanceTaskMapper.toResponseList(maintenanceTaskList);
	}

	/**
	 * 指定されたカテゴリーIDと整備タスクIDに紐づく、論理削除されていない整備タスクを検索します。
	 *
	 * @param categoryId カテゴリーのID
	 * @param maintenanceTaskId 検索対象の整備タスクID
	 * @return 検索条件に一致する整備タスクをOptionalで返します。
	 */
	public MaintenanceTaskResponse findByCategoryIdAndMaintenanceTaskId(Integer categoryId, Integer maintenanceTaskId){
		MaintenanceTask maintenanceTask =
				maintenanceTaskRepository.findByCategoryIdAndIdAndIsDeletedFalse(categoryId, maintenanceTaskId)
						.orElseThrow(() -> new ResourceNotFoundException("カテゴリーID " + categoryId + " に紐づく整備タスクID " + maintenanceTaskId + " が見つかりません。"));

		return maintenanceTaskMapper.toResponse(maintenanceTask);
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
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリーID " + request.getCategoryId() + " が見つかりません。"));

		MaintenanceTask maintenanceTask = maintenanceTaskMapper.toEntity(request, category);

		MaintenanceTask savedTask = maintenanceTaskRepository.save(maintenanceTask);

		return maintenanceTaskMapper.toResponse(savedTask);
	}

	/**
	 * 整備タスクの更新を行います。
	 *
	 * @param maintenanceTaskId 整備タスクID
	 * @param request 更新するリクエスト情報
	 * @return 更新後の整備タスク情報
	 */
	@Transactional
	public MaintenanceTaskResponse updateMaintenanceTask(Integer maintenanceTaskId ,
	                                                     MaintenanceTaskUpdateRequest request) {
		MaintenanceTask existingMaintenanceTask = maintenanceTaskRepository.findById(maintenanceTaskId)
				.orElseThrow(() -> new ResourceNotFoundException("整備タスクID " + maintenanceTaskId + " が見つかりません。"));

		MaintenanceTaskUpdateRequest updateTask = MaintenanceTaskUpdateRequest.builder()
				.name(request.getName())
				.description(request.getDescription())
				.build();

		existingMaintenanceTask.updateFrom(updateTask);

		MaintenanceTask savedTask = maintenanceTaskRepository.save(existingMaintenanceTask);

		return maintenanceTaskMapper.toResponse(savedTask);
	}

	/**
	 * 整備タスクの論理削除を行います。
	 *
	 * @param maintenanceTaskId 整備タスクID
	 */
	@Transactional
	public void softDeleteMaintenanceTask(Integer maintenanceTaskId) {
		MaintenanceTask existingMaintenanceTask = maintenanceTaskRepository.findById(maintenanceTaskId)
				.orElseThrow(() -> new ResourceNotFoundException("整備タスクID " + maintenanceTaskId + " が見つかりません。"));

		existingMaintenanceTask.softDelete();

		maintenanceTaskRepository.save(existingMaintenanceTask);
	}
}
