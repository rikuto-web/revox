package com.rikuto.revox.domain;

import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskUpdateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 整備タスク情報を表すドメインです。
 * データベースのmaintenance_tasksテーブルにマッピングされています。
 */
@Schema(description = "バイクの整備タスク情報を表すドメインです。")
@Entity
@Table(name = "maintenance_tasks")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MaintenanceTask {

	/**
	 * 整備タスクは必ず１つのカテゴリーに保持されます。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	@Schema(description = "整備タスクが属するカテゴリー情報。")
	private Category category;

	/**
	 * 整備タスクは必ず１台のバイクに保持されます。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "bike_id", nullable = false)
	@Schema(description = "整備タスクが関連するバイク情報。")
	private Bike bike;

	/**
	 * 整備タスクの一意なIDです。
	 * データベースで登録時に自動生成されます。
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "整備タスクの一意なID。")
	private int id;

	/**
	 * 整備タスクのタイトル名です。
	 * 「オイル交換の必要物品」などユーザーが自由に入力します。
	 * nullおよび空文字は許容しません。
	 */
	@Column(length = 100, nullable = false)
	@Schema(description = "整備タスクのタイトル名。ユーザーが自由に設定でき、必須項目です。", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	/**
	 * 整備タスクの詳細内容です。
	 * AIから出力された結果を元にユーザーが編集を加えたものが保持されます。
	 */
	@Column(columnDefinition = "TEXT", nullable = false)
	@Schema(description = "整備タスクの詳細内容。AIの出力やユーザーの編集内容が保持されます。", requiredMode = Schema.RequiredMode.REQUIRED)
	private String description;

	/**
	 * 論理削除フラグ。
	 * trueの場合、レコードは削除済みとして扱われます。
	 */
	@Column(name = "is_deleted", nullable = false)
	@Builder.Default
	@Schema(description = "論理削除フラグ。trueの場合、レコードは削除済みとして扱われます。")
	private boolean isDeleted = false;

	/**
	 * レコードが作成された日時
	 * DBでの自動設定に任せるためシステム側での日時の設定は行いません。
	 */
	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	@Schema(description = "レコードが作成された日時", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	/**
	 * レコードが更新された最終日時
	 * 日時はDBで自動設定されるためシステム側では日時の更新は行いません。
	 */
	@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
	@Schema(description = "レコードが更新された最終日時", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;


	/**
	 * 受け取ったリクエスト内容を更新するメソッドです。
	 * 日時はDBで自動設定されるためシステム側では日時の更新は行いません。
	 *
	 * @param request 更新する整備タスク
	 */
	public void updateFrom(MaintenanceTaskUpdateRequest request) {
		if(request.getName() != null) {
			this.name = request.getName();
		}
		if(request.getDescription() != null) {
			this.description = request.getDescription();
		}
	}

	/**
	 * 論理削除のためのメソッドです。
	 * 日時はDBで自動設定されるためシステム側では日時の更新は行いません。
	 */
	public void softDelete() {
		this.isDeleted = true;
	}
}