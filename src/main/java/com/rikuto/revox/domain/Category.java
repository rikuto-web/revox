package com.rikuto.revox.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * カテゴリー情報を表すエンティティです。
 * データベースのcategoriesにマッピングされています。
 * カテゴリー情報は事前にデータベースに登録されておりユーザーからの受け付けは行いまえん。
 * 管理者がカテゴリー管理するため論理削除は実装しません。
 */
@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Category {

	/**
	 * このカテゴリーが保有する整備タスクのリストです。
	 * maintenanceTasksエンティティとの1対多のリレーションシップを表現しています。
	 */
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MaintenanceTask> maintenanceTasks = new ArrayList<>();

	/**
	 * カテゴリーの一意なID。
	 * データベースで登録時に自動生成されます。
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	/**
	 * データベースに登録されているカテゴリーから詳細検索を行うためのカテゴリー名です。
	 */
	@Column(length = 50, unique = true, nullable = false)
	@Size(max = 50)
	@NotBlank
	private String name;

	/**
	 * 登録されているカテゴリーに割り振られた番号です。
	 * ユーザー側への表示の際にカテゴリーを適切な並び順で認識させます。
	 */
	@Column(name = "display_order")
	private Integer displayOrder;

	/**
	 * レコードが作成された日時
	 */
	@Column(name = "created_at", nullable = false)
	@NonNull
	private LocalDateTime createdAt;

	/**
	 * レコードが更新された最終日時
	 */
	@Column(name = "updated_at", nullable = false)
	@NonNull
	private LocalDateTime updatedAt;
}
