package com.rikuto.revox.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ユーザーのバイク整備に関するサービス記録、およびAIアドバイスの情報を表すエンティティです。
 * データベースのservice_recordsテーブルにマッピングされています。
 */
@Entity
@Table(name = "service_records")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ServiceRecord {

	/**
	 * AI情報の一意なIDです。
	 * データベースで登録時に自動生成されます。
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	/**
	 * このサービス記録が紐づくユーザー情報です。
	 * サービス記録は必ず特定のユーザーによって作成されます。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/**
	 * このサービス記録が紐づくバイク情報です。
	 * サービス記録は必ず特定のバイクに対して作成されます。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "bike_id", nullable = false)
	private Bike bike;

	/**
	 * このサービス記録が紐づく整備タスク情報です。
	 * サービス記録は必ず特定の整備タスクに関連付けられます。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "maintenance_task_id", nullable = false)
	private MaintenanceTask maintenanceTask;

	/**
	 * AIがサービス記録を出力するためのテキスト情報です。
	 * AIを使用しないユーザーを考慮してnullを許容しています。
	 */
	@Column(name = "ai_advice_original", columnDefinition = "TEXT")
	private String aiAdviceOriginal;

	/**
	 * AIの出力結果に対する変更およびユーザーが入力した情報を保持するテキスト情報です。
	 * AIの使用の有無を問わずNullを許容しませんが空文字は許容します。
	 */
	@Column(name = "user_edited_content", columnDefinition = "TEXT", nullable = false)
	@NonNull
	private String userEditedContent;

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

