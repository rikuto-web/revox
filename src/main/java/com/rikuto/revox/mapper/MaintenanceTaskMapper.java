package com.rikuto.revox.mapper;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.Bike;
import com.rikuto.revox.domain.MaintenanceTask;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MaintenanceTaskドメインと関連するDTOのマッピングを行うクラスです。
 */
@Component
public class MaintenanceTaskMapper {

	/**
	 * 単一のMaintenanceTaskドメインをMaintenanceTaskResponse DTOに変換します。
	 *
	 * @param task 変換する MaintenanceTaskドメイン。
	 * @return 変換された MaintenanceTaskResponse DTO。
	 */
	public MaintenanceTaskResponse toResponse(MaintenanceTask task) {

		return MaintenanceTaskResponse.builder()
				.id(task.getId())
				.bikeId(task.getBike() != null ? task.getBike().getId() : null)
				.categoryId(task.getCategory() != null ? task.getCategory().getId() : null)

				.name(task.getName())
				.description(task.getDescription())

				.createdAt(task.getCreatedAt())
				.updatedAt(task.getUpdatedAt())
				.build();
	}

	/**
	 * MaintenanceTaskドメインリストをMaintenanceTaskResponse DTOのリストに変換します。
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
	 * @param request  整備タスク作成リクエストを含む MaintenanceTaskRequest DTO。
	 * @param category 整備タスクを所有する Categoryドメイン。
	 * @return 作成された MaintenanceTaskドメイン。
	 */
	public MaintenanceTask toDomain(MaintenanceTaskRequest request, Bike bike, Category category) {

		return MaintenanceTask.builder()
				.category(category)
				.bike(bike)

				.name(request.getName())
				.description(request.getDescription())
				.build();
	}
}
