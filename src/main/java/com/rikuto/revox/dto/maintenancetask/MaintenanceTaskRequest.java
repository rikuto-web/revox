package com.rikuto.revox.dto.maintenancetask;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ユーザーから受け取る整備タスクのリクエストです。
 * 各フィールドにはバリデーションがあります。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "新しい整備タスクを登録するためのリクエストDTOです。")
public class MaintenanceTaskRequest {

	@NotNull(message = "カテゴリーIDは必須です。")
	@Min(value = 1, message = "カテゴリーIDは1以上である必要があります。")
	@Schema(description = "整備タスクが属するカテゴリーの一意なID。必須項目です。", example = "1")
	private Integer categoryId;

	@NotNull(message = "バイクIDは必須です。")
	@Min(value = 1, message = "バイクIDは1以上である必要があります。")
	@Schema(description = "整備タスクを紐づけるバイクの一意なID。必須項目です。", example = "10")
	private Integer bikeId;

	@NotBlank(message = "タスク名は必須です。")
	@Size(max = 100, message = "タスク名は100文字以内で入力してください。")
	@Schema(description = "整備タスクのタイトル。100文字以内で入力してください。", example = "オイル交換の必要物品")
	private String name;

	@NotBlank(message = "詳細内容は必須です。")
	@Size(max = 5000, message = "質問内容は5000文字以内で入力してください。")
	@Schema(description = "整備タスクの詳細内容。5000文字以内で入力してください。", example = "エンジンの状態を確認し、必要な工具を準備する。")
	private String description;
}