package com.rikuto.revox.mapper;

import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.MaintenanceTask;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MaintenanceTaskドメインと関連するDTOの相互変換を担うクラスです。
 * ドメインの振る舞い（更新・削除などのロジック）は保持せず、純粋なデータ変換に専念します。
 */
@Component
public class MaintenanceTaskMapper {

	/**
	 * 単一のMaintenanceTaskドメインをMaintenanceTaskResponse DTOに変換します。
	 * @param task 変換する MaintenanceTaskドメイン。
	 * @return 変換された MaintenanceTaskResponse DTO。
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
	 * MaintenanceTaskドメインのリストをMaintenanceTaskResponse DTOのリストに変換します。
	 *
	 * @param maintenanceTaskList 変換対象のMaintenanceTaskドメインのリスト。
	 * @return 変換されたMaintenanceTaskResponse DTOのリスト。
	 */
	public List<MaintenanceTaskResponse> toResponseList(List<MaintenanceTask> maintenanceTaskList) {
		return maintenanceTaskList.stream()
				.map(this::toResponse)
				.toList();
	}


	/**
	 * MaintenanceTaskRequest DTOと Categoryドメインから新しい MaintenanceTaskドメインを作成します。
	 *
	 * @param request 整備タスク作成リクエストを含む MaintenanceTaskRequest DTO。
	 * @param category  整備タスクを所有する Categoryドメイン。
	 * @return 作成された MaintenanceTaskドメイン。
	 */
	public MaintenanceTask toEntity (MaintenanceTaskRequest request, Category category) {
	return MaintenanceTask.builder()
			.name(request.getName())
			.category(category)
			.description(request.getDescription())
			.build();
	}
}
