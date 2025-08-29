package com.rikuto.revox.dto.maintenancetask;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 整備タスクに対するレスポンス内容のDTOです。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "整備タスクの詳細情報を表すレスポンスDTOです。")
public class MaintenanceTaskResponse {

	@Schema(description = "整備タスクの一意なID。")
	private Integer id;

	@Schema(description = "整備タスクが属するカテゴリーの一意なID。")
	private Integer categoryId;

	@Schema(description = "整備タスクが関連するバイクの一意なID。")
	private Integer bikeId;

	@Schema(description = "整備タスクのタイトル名。")
	private String name;

	@Schema(description = "整備タスクの詳細内容。")
	private String description;

	@Schema(description = "レコードが作成された日時。")
	private LocalDateTime createdAt;

	@Schema(description = "レコードが更新された最終日時。")
	private LocalDateTime updatedAt;
}