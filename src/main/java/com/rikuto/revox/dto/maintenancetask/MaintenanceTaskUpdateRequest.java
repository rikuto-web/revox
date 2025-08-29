package com.rikuto.revox.dto.maintenancetask;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 整備タスク情報を更新する際に受け取るリクエストです。
 * 各フィールドにはバリデーションがあります。
 */
@Getter
@Setter
@Builder
@Schema(description = "整備タスクを更新するためのリクエストDTOです。")
public class MaintenanceTaskUpdateRequest {

	@NotBlank(message = "タスク名は必須です。")
	@Size(max = 100, message = "タスク名は100文字以内で入力してください。")
	@Schema(description = "整備タスクの新しいタイトル。100文字以内で入力してください。", example = "キャブレターの清掃")
	private String name;

	@NotBlank(message = "詳細内容は必須です。")
	@Size(max = 5000, message = "質問内容は5000文字以内で入力してください。")
	@Schema(description = "整備タスクの新しい詳細内容。5000文字以内で入力してください。", example = "キャブレターを取り外し、専用クリーナーで内部を清掃する。")
	private String description;
}