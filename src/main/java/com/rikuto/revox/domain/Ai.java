package com.rikuto.revox.domain;

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
 * AIへの質問および回答情報を表すドメインです。
 * データベースのai_questionsテーブルにマッピングされています。
 */
@Schema(description = "AIへの質問および回答情報を表すドメイン")
@Entity
@Table(name = "ai_questions")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Ai {

	/**
	 * AI質問の一意なIDです。
	 * データベースで登録時に自動生成されます。
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "AI質問の一意なIDです。")
	private int id;

	/**
	 * この質問を投稿したユーザー情報です。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@Schema(description = "この質問を投稿したユーザー情報です。")
	private User user;

	/**
	 * ユーザーの保有する特定のバイク情報です。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "bike_id", nullable = false)
	@Schema(description = "ユーザーの保有する特定のバイク情報です。")
	private Bike bike;

	/**
	 * 質問に関連するカテゴリー情報です。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	@Schema(description = "質問に関連するカテゴリー情報です。")
	private Category category;

	/**
	 * ユーザーが入力した質問内容です。
	 */
	@Column(name = "question", columnDefinition = "TEXT", nullable = false)
	@Schema(description = "ユーザーが入力した質問内容です。", requiredMode = Schema.RequiredMode.REQUIRED)
	private String question;

	/**
	 * AIが生成した回答内容です。
	 */
	@Column(name = "answer", columnDefinition = "TEXT", nullable = false)
	@Schema(description = "AIが生成した回答内容です。", requiredMode = Schema.RequiredMode.REQUIRED)
	private String answer;

	/**
	 * レコードが作成された日時
	 * 日時はDBで自動設定されるためシステム側では日時の更新は行いません。
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
}