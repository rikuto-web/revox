package com.rikuto.revox.mapper;

import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.MaintenanceTask;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MaintenanceTaskエンティティと関連するDTOの相互変換を担うクラスです。
 * ドメインの振る舞い（更新・削除などのロジック）は保持せず、純粋なデータ変換に専念します。
 */
@Component
public class MaintenanceTaskMapper {

	/**
	 * ユーザーへレスポンスする内容へ変換します。
	 * @param task 整備タスク情報
	 * @return フロント側へ渡す単一の整備タスク情報
	 */
	public MaintenanceTaskResponse toResponse(MaintenanceTask task) {
		return MaintenanceTaskResponse.builder()
				.id(task.getId())
				.name(task.getName())
				.description(task.getDescription())
				.createdAt(task.getCreatedAt())
				.updatedAt(task.getUpdatedAt())
				.categoryId(task.getCategory() != null ? task.getCategory().getId() : null)
				.build();
	}

	/**
	 * ユーザーへレスポンスする内容へ変換します。
	 * 整備タスクをList化します
	 *
	 * @param maintenanceTaskList 複数の整備タスク
	 * @return List化された整備タスク
	 */
	public List<MaintenanceTaskResponse> toResponseList(List<MaintenanceTask> maintenanceTaskList) {
		return maintenanceTaskList.stream()
				.map(this::toResponse)
				.toList();
	}


	/**
	 * 受け取ったリクエスト情報をEntityに変換します
	 * @param request リクエストとして受け取った整備タスク情報
	 * @param category カテゴリー情報
	 * @return Entity情報
	 */
	public MaintenanceTask toEntity (MaintenanceTaskRequest request, Category category) {
	return MaintenanceTask.builder()
			.name(request.getName())
			.category(category)
			.description(request.getDescription())
			.build();
	}
}
