package com.rikuto.revox.service;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.maintenancetask.MaintenanceTask;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.MaintenanceTaskMapper;
import com.rikuto.revox.repository.BikeRepository;
import com.rikuto.revox.repository.CategoryRepository;
import com.rikuto.revox.repository.MaintenanceTaskRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	private final BikeRepository bikeRepository;

	private final MaintenanceTaskMapper maintenanceTaskMapper;

	public MaintenanceTaskService(CategoryRepository categoryRepository,
	                              MaintenanceTaskRepository maintenanceTaskRepository,
	                              MaintenanceTaskMapper maintenanceTaskMapper,
	                              BikeRepository bikeRepository) {
		this.categoryRepository = categoryRepository;
		this.maintenanceTaskRepository = maintenanceTaskRepository;
		this.maintenanceTaskMapper = maintenanceTaskMapper;
		this.bikeRepository = bikeRepository;
	}

	// CREATE
	//------------------------------------------------------------------------------------------------------------------
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

		Bike bike = bikeRepository.findById(request.getBikeId())
				.orElseThrow(() -> new ResourceNotFoundException("バイクID " + request.getBikeId() + " が見つかりません。"));

		MaintenanceTask maintenanceTask = maintenanceTaskMapper.toEntity(request, bike, category);

		MaintenanceTask savedTask = maintenanceTaskRepository.save(maintenanceTask);

		return maintenanceTaskMapper.toResponse(savedTask);
	}

	// READ
	//------------------------------------------------------------------------------------------------------------------
	/**
	 * ユーザーIDに紐づく最新の整備タスクを指定件数分検索します。
	 *
	 * @param userId ユーザーID
	 * @return ユーザーIDに紐づく最新の整備タスクリスト
	 */
	public List<MaintenanceTaskResponse> findLatestMaintenanceTasksByUserId(Integer userId) {
		Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());

		List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(userId, pageable);

		return maintenanceTaskMapper.toResponseList(tasks);
	}

	/**
	 * 指定されたバイクIDに紐づく、論理削除されていないすべての整備タスクを検索します。
	 *
	 * @param bikeId バイクID
	 * @return バイクIDに紐づく整備タスクリスト
	 */
	public List<MaintenanceTaskResponse> findByBikeId(Integer bikeId) {
		List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(bikeId);

		return maintenanceTaskMapper.toResponseList(tasks);
	}

	/**
	 * 指定されたバイクIDとカテゴリーIDに紐づく、論理削除されていないすべての整備タスクを検索します。
	 *
	 * @param bikeId バイクID
	 * @param categoryId カテゴリーID
	 * @return バイクIDとカテゴリーIDで絞り込んだ整備タスクリスト
	 */
	public List<MaintenanceTaskResponse> findByBikeIdAndCategoryId(Integer bikeId, Integer categoryId) {
		List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(bikeId, categoryId);

		return maintenanceTaskMapper.toResponseList(tasks);
	}

	/**
	 * 指定されたカテゴリーIDと整備タスクIDに紐づく、論理削除されていない整備タスクを検索します。
	 *
	 * @param categoryId カテゴリーID
	 * @param maintenanceTaskId 整備タスクID
	 * @return 検索条件に一致する整備タスクをOptionalで返します。
	 */
	public MaintenanceTaskResponse findByCategoryIdAndMaintenanceTaskId(Integer categoryId, Integer maintenanceTaskId) {
		MaintenanceTask maintenanceTask = maintenanceTaskRepository.findByCategoryIdAndIdAndIsDeletedFalse(categoryId, maintenanceTaskId)
						.orElseThrow(() -> new ResourceNotFoundException("カテゴリーID " + categoryId + " に紐づく整備タスクID " + maintenanceTaskId + " が見つかりません。"));

		return maintenanceTaskMapper.toResponse(maintenanceTask);
	}

	// UPDATE
	//------------------------------------------------------------------------------------------------------------------
	/**
	 * 整備タスクの更新を行います。
	 *
	 * @param maintenanceTaskId 整備タスクID
	 * @param request           更新するリクエスト情報
	 * @return 更新後の整備タスク情報
	 */
	@Transactional
	public MaintenanceTaskResponse updateMaintenanceTask(Integer maintenanceTaskId,
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

	// DELETE
	//------------------------------------------------------------------------------------------------------------------
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