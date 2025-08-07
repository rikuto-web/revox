package com.rikuto.revox.controller;

import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.service.MaintenanceTaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 整備タスク情報のCRUD操作を扱うコントローラーです。
 */
@RestController
@RequestMapping("api/maintenance-task")
public class MaintenanceTaskController {

	private final MaintenanceTaskService maintenanceTaskService;

	public MaintenanceTaskController(MaintenanceTaskService maintenanceTaskService) {
		this.maintenanceTaskService = maintenanceTaskService;
	}

	/**
	 * カテゴリーIDに紐づいた整備タスクを全件検索します。
	 * GET /api/maintenance-task/category/{categoryId}
	 *
	 * @param categoryId カテゴリーID
	 * @return 整備タスク情報とHttpステータス　200　OK
	 */
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<MaintenanceTaskResponse>> getMaintenanceTaskByCategoryId(@PathVariable Integer categoryId) {
		List<MaintenanceTaskResponse> maintenanceTaskResponse =
				maintenanceTaskService.findMaintenanceTaskByCategoryId(categoryId);

		return ResponseEntity.ok(maintenanceTaskResponse);
	}

	/**
	 * 整備タスクの新規登録を行います。
	 * POST /api/maintenance-task
	 *
	 * @param request 登録する整備タスク情報
	 * @return 登録済の整備タスク情報とHttpステータス　201
	 */
	@PostMapping
	public ResponseEntity<MaintenanceTaskResponse> registerMaintenanceTask(@Valid @RequestBody MaintenanceTaskRequest request) {
		MaintenanceTaskResponse registeredMaintenanceTask =
				maintenanceTaskService.registerMaintenanceTask(request);

		return new ResponseEntity<>(registeredMaintenanceTask, HttpStatus.CREATED);
	}

	/**
	 * 整備タスクの更新を行います。
	 * PUT /api/maintenance-task/{maintenanceTaskId}
	 *
	 * @param maintenanceTaskId 整備タスクID
	 * @param request 更新する内容の整備タスク
	 * @return 更新後の整備タスク情報とHttpステータス　200　ok
	 */
	@PutMapping("/{maintenanceTaskId}")
	public ResponseEntity<MaintenanceTaskResponse> updateMaintenanceTask(@PathVariable Integer maintenanceTaskId,
	                                                                     @RequestBody @Valid MaintenanceTaskRequest request) {
		MaintenanceTaskResponse maintenanceTaskResponse =
				maintenanceTaskService.updateMaintenanceTask(maintenanceTaskId, request);

		return ResponseEntity.ok(maintenanceTaskResponse);
	}

	/**
	 * 整備タスクの論理削除を行います。
	 * PATCH /api/maintenance-task/{maintenanceTaskId}
	 *
	 * @param maintenanceTaskId 整備タスクID
	 * @return Httpステータス　204
	 */
	@PatchMapping("/{maintenanceTaskId}")
	public ResponseEntity<Void> softDeleteMaintenanceTask(@PathVariable Integer maintenanceTaskId) {
		maintenanceTaskService.softDeleteMaintenanceTask(maintenanceTaskId);
		return ResponseEntity.noContent().build();
	}
}
