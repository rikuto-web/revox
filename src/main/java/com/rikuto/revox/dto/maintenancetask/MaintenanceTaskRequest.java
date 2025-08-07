package com.rikuto.revox.dto.maintenancetask;

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
public class MaintenanceTaskRequest {

	@NotNull(message = "カテゴリーIDは必須です。")
	@Min(value = 1, message = "カテゴリーIDは1以上である必要があります。")
	private Integer categoryId;

	@NotBlank(message = "タスク名は必須です。")
	@Size(max = 100, message = "タスク名は100文字以内で入力してください。")
	private String name;

	@NotBlank(message = "詳細内容は必須です。")
	@Size(max = 5000, message = "質問内容は5000文字以内で入力してください。")
	private String description;
}
