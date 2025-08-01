package com.rikuto.revox.domain;

import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 整備タスク情報を表すエンティティです。
 * データベースのmaintenance_tasksテーブルにマッピングされています。
 */
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
	private Category category;

	/**
	 * 整備タスクの一意なIDです。
	 * データベースで登録時に自動生成されます。
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	/**
	 * 整備タスクのタイトル名です。
	 * 「オイル交換の必要物品」などユーザーが自由に入力します。
	 * nullおよび空文字は許容しません。
	 */
	@Column(length = 100, nullable = false)
	private String name;

	/**
	 * 整備タスクの詳細内容です。
	 * AIから出力された結果を元にユーザーが編集を加えたものが保持されます。
	 * タスクのみ作成しておく可能性も考慮しnullを許容しています。
	 * DB依存性を下げるためLobを使用しています。
	 */
	@Lob
	@Column
	private String description;

	/**
	 * 論理削除フラグ。
	 * trueの場合、レコードは削除済みとして扱われます。
	 */
	@Column(name = "is_deleted", nullable = false)
	@Builder.Default
	private boolean isDeleted = false;

	/**
	 * レコードが作成された日時
	 * DBでの自動設定に任せるためシステム側での日時の設定は行いません。
	 */
	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	/**
	 * レコードが更新された最終日時
	 * 日時はDBで自動設定されるためシステム側では日時の更新は行いません。
	 */
	@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;


	/**
	 * リクエスト内容への更新メソッドです。
	 * 日時はDBで自動設定されるためシステム側では日時の更新は行いません。
	 * @param request 更新する整備タスク
	 */
	public void updateFrom(MaintenanceTaskRequest request) {
		this.name = request.getName();
		this.description = request.getDescription();
	}

	/**
	 * 論理削除のためのメソッドです。
	 *日時はDBで自動設定されるためシステム側では日時の更新は行いません。
	 */
	public void softDelete() {
		this.isDeleted = true;
	}
}
