package com.rikuto.revox.dto.maintenancetask;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MaintenanceTaskUpdateRequest {

	@NotBlank(message = "タスク名は必須です。")
	@Size(max = 100, message = "タスク名は100文字以内で入力してください。")
	private String name;

	@NotBlank(message = "詳細内容は必須です。")
	@Size(max = 5000, message = "質問内容は5000文字以内で入力してください。")
	private String description;
}
