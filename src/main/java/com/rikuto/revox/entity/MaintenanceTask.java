package com.rikuto.revox.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
	 * １つまたは複数の整備タスクは必ず１つのカテゴリーに保持されます。
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
	@Size(max = 100)
	@NotBlank
	private String name;

	/**
	 * 整備タスクの詳細内容です。
	 * AIから出力された結果を元にユーザーが編集を加えたものが保持されます。
	 * タスクのみ作成しておく可能性も考慮しnullを許容しています。
	 */
	@Column(columnDefinition = "TEXT")
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
	 */
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	/**
	 * レコードが更新された最終日時
	 */
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}
