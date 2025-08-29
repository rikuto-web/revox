package com.rikuto.revox.controller;

import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskUpdateRequest;
import com.rikuto.revox.service.MaintenanceTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 整備タスクに関するコントローラーです。
 */
@Tag(name = "整備タスクに関する管理", description = "バイクの整備タスク情報の登録、取得、更新、削除を管理するエンドポイント群です。")
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
	 */
	@Operation(summary = "整備タスクを新規登録する", description = "新しい整備タスクをシステムに登録します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "整備タスクの登録に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = MaintenanceTaskResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（バリデーションエラーなど）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない")
	})
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<MaintenanceTaskResponse> registerMaintenanceTask(
			@RequestBody @Valid MaintenanceTaskRequest request
	) {
		MaintenanceTaskResponse registerMaintenanceTask = maintenanceTaskService.registerMaintenanceTask(request);
		return new ResponseEntity<>(registerMaintenanceTask, HttpStatus.CREATED);
	}

	// READ
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * ユーザーIDに紐づく整備タスクを取得します。
	 * ダッシュボードでの最新記録表示に利用します。
	 */
	@Operation(summary = "ユーザーの最新整備タスクリストを取得する", description = "指定されたユーザーIDに紐づく最新の整備タスク情報をリスト形式で取得します。ダッシュボードでの最新記録表示に利用されます。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "整備タスクリストの取得に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = MaintenanceTaskResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（ユーザーIDが不正など）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない")
	})
	@GetMapping("/user/{userId}")
	@PreAuthorize("hasAnyRole('GUEST', 'USER')")
	public ResponseEntity<List<MaintenanceTaskResponse>> getLatestMaintenanceTasksByUserId(
			@Parameter(description = "整備タスクリストを取得したいユーザーの一意の識別子。", required = true)
			@PathVariable @Positive Integer userId
	) {
		List<MaintenanceTaskResponse> responseListByUserId = maintenanceTaskService.findLatestMaintenanceTasksByUserId(userId);
		return ResponseEntity.ok(responseListByUserId);
	}

	/**
	 * バイクIDに紐づいた整備タスクを取得します。
	 */
	@Operation(summary = "特定のバイクの全整備タスクを取得する", description = "指定されたバイクIDに紐づく全ての整備タスクをリスト形式で取得します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "整備タスクリストの取得に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = MaintenanceTaskResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（バイクIDが不正など）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない")
	})
	@GetMapping("/bike/{bikeId}")
	@PreAuthorize("hasAnyRole('GUEST', 'USER')")
	public ResponseEntity<List<MaintenanceTaskResponse>> getMaintenanceTaskByBikeID(
			@Parameter(description = "整備タスクを取得したいバイクの一意の識別子。", required = true)
			@PathVariable @Positive Integer bikeId
	) {
		List<MaintenanceTaskResponse> responseListByBikeId = maintenanceTaskService.findByBikeId(bikeId);
		return ResponseEntity.ok(responseListByBikeId);
	}

	/**
	 * バイクIDとカテゴリーIDに紐づいた整備タスクを取得します。
	 */
	@Operation(summary = "バイクとカテゴリーで整備タスクを絞り込んで取得する", description = "指定されたバイクIDとカテゴリーIDで絞り込んだ整備タスクのリストを取得します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "整備タスクリストの取得に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = MaintenanceTaskResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（IDが不正など）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない")
	})
	@GetMapping("/bike/{bikeId}/category/{categoryId}")
	@PreAuthorize("hasAnyRole('GUEST', 'USER')")
	public ResponseEntity<List<MaintenanceTaskResponse>> getMaintenanceTaskByBikeIdAndCategoryId(
			@Parameter(description = "整備タスクを取得したいバイクの一意の識別子。", required = true)
			@PathVariable("bikeId") @Positive Integer bikeId,
			@Parameter(description = "整備タスクを取得したいカテゴリーの一意の識別子。", required = true)
			@PathVariable("categoryId") @Positive Integer categoryId
	) {
		List<MaintenanceTaskResponse> responseList = maintenanceTaskService.findByBikeIdAndCategoryId(bikeId, categoryId);
		return ResponseEntity.ok(responseList);
	}

	// UPDATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 整備タスクの更新を行います。
	 */
	@Operation(summary = "整備タスクを更新する", description = "既存の整備タスク情報を部分的に更新します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "整備タスクの更新に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = MaintenanceTaskResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（バリデーションエラーなど）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない"),
			@ApiResponse(responseCode = "404", description = "整備タスクが見つからない")
	})
	@PatchMapping("/{maintenanceTaskId}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<MaintenanceTaskResponse> updateMaintenanceTask(
			@Parameter(description = "更新したい整備タスクの一意の識別子。", required = true)
			@PathVariable @Positive Integer maintenanceTaskId,
			@RequestBody @Valid MaintenanceTaskUpdateRequest request
	) {
		MaintenanceTaskResponse updateTask = maintenanceTaskService.updateMaintenanceTask(maintenanceTaskId, request);
		return ResponseEntity.ok(updateTask);
	}

	// DELETE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 整備タスクの論理削除を行います。
	 */
	@Operation(summary = "整備タスクを論理削除する", description = "指定された整備タスクを物理的に削除するのではなく、論理的に削除（非表示）にします。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "整備タスクの論理削除に成功"),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（IDが不正など）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない"),
			@ApiResponse(responseCode = "404", description = "整備タスクが見つからない")
	})
	@PatchMapping("/{maintenanceTaskId}/softDelete")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Void> softDeleteMaintenanceTask(
			@Parameter(description = "論理削除したい整備タスクの一意の識別子。", required = true)
			@PathVariable @Positive Integer maintenanceTaskId
	) {
		maintenanceTaskService.softDeleteMaintenanceTask(maintenanceTaskId);
		return ResponseEntity.noContent().build();
	}
}