package com.rikuto.revox.domain;

import com.rikuto.revox.dto.bike.BikeUpdateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * バイク情報を表すドメインです。
 * データベースのbikesテーブルにマッピングされています。
 */
@Schema(description = "ユーザーが所有するバイクの情報を表すドメインです。")
@Entity
@Table(name = "bikes")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Bike {

	/**
	 * １台または複数台のバイクは必ず１人のユーザーに保持されます。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@Schema(description = "バイクを所有するユーザー情報。")
	private User user;

	/**
	 * １台のバイクは必ず複数の整備タスクを保持します。
	 */
	@OneToMany(mappedBy = "bike", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	@Schema(description = "このバイクに紐づく整備タスクのリスト。")
	private List<MaintenanceTask> maintenanceTasks = new ArrayList<>();

	/**
	 * バイクの一意なIDです。
	 * データベースで登録時に自動生成されます。
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "バイクの一意なID。データベースで自動生成されます。")
	private int id;

	/**
	 * ユーザーが保有するバイクのメーカー情報です。
	 * 「ホンダ」「HONDA」などユーザーの好みの入力を受け付けます。
	 * Nullは許容しません。
	 */
	@Column(name = "manufacturer", length = 50, nullable = false)
	@Schema(description = "バイクのメーカー名。必須項目です。")
	private String manufacturer;

	/**
	 * ユーザーが保有するバイクの車両名です。
	 * 「レブル」「Rebel」など好みの入力を受け付けます。
	 * Nullは許容しません。
	 */
	@Column(name = "model_name", length = 100, nullable = false)
	@Schema(description = "バイクの車両名。必須項目です。")
	private String modelName;

	/**
	 * ユーザーが保有するバイクの型式です。
	 * 分からないユーザーもいるためnullは許容します。
	 */
	@Column(name = "model_code", length = 20)
	@Schema(description = "バイクの型式。未入力も可能です。")
	private String modelCode;

	/**
	 * ユーザーが保有するバイクの年式です。
	 * 分からないユーザーもいるためnullは許容します。
	 */
	@Column(name = "model_year")
	@Schema(description = "バイクの年式。未入力も可能です。")
	private Integer modelYear;

	/**
	 * ユーザーが保有するバイクの走行距離です。
	 * 走行距離を入力することでメンテナンス時期の指標とします。
	 * 未入力でも許容するためnullを許容します。
	 */
	@Column(name = "current_mileage")
	@Schema(description = "バイクの現在の走行距離。メンテナンス時期の指標となります。")
	private Integer currentMileage;

	/**
	 * ユーザーが保有するバイクの購入日です。
	 * 未入力でも許容するためnullを許容します。
	 */
	@Column(name = "purchase_date")
	@Schema(description = "バイクの購入日。")
	private LocalDate purchaseDate;

	/**
	 * ユーザーが保有するバイクの画像です。
	 * 未入力でも許容するためnullを許容します。
	 */
	@Column(name = "image_url", length = 2048)
	@Schema(description = "バイクの画像URL。")
	private String imageUrl;

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


	/**
	 * 受け取ったリクエスト内容を更新するメソッドです。
	 * 日時はDBで自動設定されるためシステム側では日時の更新は行いません。
	 *
	 * @param data 更新するバイク情報
	 */
	public void updateFrom(BikeUpdateRequest data) {
		if(data.getManufacturer() != null) {
			this.manufacturer = data.getManufacturer();
		}
		if(data.getModelName() != null) {
			this.modelName = data.getModelName();
		}
		if(data.getModelCode() != null) {
			this.modelCode = data.getModelCode();
		}
		if(data.getModelYear() != null) {
			this.modelYear = data.getModelYear();
		}
		if(data.getCurrentMileage() != null) {
			this.currentMileage = data.getCurrentMileage();
		}
		if(data.getPurchaseDate() != null) {
			this.purchaseDate = data.getPurchaseDate();
		}
		if(data.getImageUrl() != null) {
			this.imageUrl = data.getImageUrl();
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