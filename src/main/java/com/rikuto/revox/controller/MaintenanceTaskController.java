package com.rikuto.revox.controller;

import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskUpdateRequest;
import com.rikuto.revox.service.MaintenanceTaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 整備タスクに関するCRUD操作を扱うコントローラーです。
 */
@RestController
@RequestMapping("/api/maintenance-task")
public class MaintenanceTaskController {

	private final MaintenanceTaskService maintenanceTaskService;

	public MaintenanceTaskController(MaintenanceTaskService maintenanceTaskService) {
		this.maintenanceTaskService = maintenanceTaskService;
	}

	// CREATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 整備タスクの新規登録を行います。
	 * POST /api/maintenance-task
	 *
	 * @param request 登録する整備タスク情報
	 * @return 登録済みの整備タスク情報
	 */
	@PostMapping
	public ResponseEntity<MaintenanceTaskResponse> registerMaintenanceTask(@RequestBody @Valid MaintenanceTaskRequest request) {
		MaintenanceTaskResponse registerTask = maintenanceTaskService.registerMaintenanceTask(request);

		return new ResponseEntity<>(registerTask, HttpStatus.CREATED);
	}

	// READ
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * ユーザーIDに紐づく整備タスクを取得します。
	 * ダッシュボードでの最新記録表示に利用します。
	 * GET /api/maintenance-task/user/{userId}
	 *
	 * @param userId ユーザーID
	 * @return ユーザーIDに紐づく全ての整備タスク
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<MaintenanceTaskResponse>> getLatestMaintenanceTasksByUserId(@PathVariable @Positive Integer userId) {
		List<MaintenanceTaskResponse> responseList = maintenanceTaskService.findLatestMaintenanceTasksByUserId(userId);

		return ResponseEntity.ok(responseList);
	}

	/**
	 * バイクIDに紐づいた整備タスクを取得します。
	 * GET /api/maintenance-task/bike/{bikeId}
	 *
	 * @param bikeId バイクID
	 * @return バイクIDに紐づいた整備タスクリスト
	 */
	@GetMapping("/bike/{bikeId}")
	public ResponseEntity<List<MaintenanceTaskResponse>> getMaintenanceTaskByBikeID(@PathVariable @Positive Integer bikeId) {
		List<MaintenanceTaskResponse> responseList = maintenanceTaskService.findByBikeId(bikeId);

		return ResponseEntity.ok(responseList);
	}

	/**
	 * バイクIDとカテゴリーIDに紐づいた整備タスクを取得します。
	 * GET /api/maintenance-task/bike/{bikeId}/category/{categoryId}
	 *
	 * @param bikeId バイクID
	 * @param categoryId カテゴリーID
	 * @return バイクIDとカテゴリーIDで絞り込んだ整備タスクリスト
	 */
	@GetMapping("/bike/{bikeId}/category/{categoryId}")
	public ResponseEntity<List<MaintenanceTaskResponse>> getMaintenanceTaskByBikeIdAndCategoryId(@PathVariable("bikeId") @Positive Integer bikeId,
	                                                                                             @PathVariable("categoryId") @Positive Integer categoryId) {
		List<MaintenanceTaskResponse> responseList = maintenanceTaskService.findByBikeIdAndCategoryId(bikeId, categoryId);

		return ResponseEntity.ok(responseList);
	}

	// UPDATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 整備タスクの更新を行います。
	 * PATCH /api/maintenance-task/{maintenanceTaskId}
	 *
	 * @param maintenanceTaskId 整備タスクID
	 * @param request           更新する内容の整備タスク
	 * @return 更新後の整備タスク情報
	 */
	@PatchMapping("/{maintenanceTaskId}")
	public ResponseEntity<MaintenanceTaskResponse> updateMaintenanceTask(@PathVariable @Positive Integer maintenanceTaskId,
	                                                                     @RequestBody @Valid MaintenanceTaskUpdateRequest request) {
		MaintenanceTaskResponse updateTask = maintenanceTaskService.updateMaintenanceTask(maintenanceTaskId, request);

		return ResponseEntity.ok(updateTask);
	}

	// DELETE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 整備タスクの論理削除を行います。
	 * PATCH /api/maintenance-task/{maintenanceTaskId}/softDelete
	 *
	 * @param maintenanceTaskId 整備タスクID
	 * @return Httpステータス　204
	 */
	@PatchMapping("/{maintenanceTaskId}/softDelete")
	public ResponseEntity<Void> softDeleteMaintenanceTask(@PathVariable @Positive Integer maintenanceTaskId) {
		maintenanceTaskService.softDeleteMaintenanceTask(maintenanceTaskId);

		return ResponseEntity.noContent().build();
	}
}