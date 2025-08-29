package com.rikuto.revox.domain;

import com.rikuto.revox.dto.user.UserUpdateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ユーザ情報を表すドメインです。
 * データベースのusersテーブルにマッピングされています。
 */
@Schema(description = "ユーザー情報を表すドメイン。登録、更新、論理削除が可能です。")
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

	/**
	 * ユーザーが所有するバイクのリストです。
	 * Bikeエンティティとの1対多のリレーションシップを表現しています。
	 */
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@Builder.Default
	@Schema(description = "ユーザーが所有するバイクのリスト。")
	private List<Bike> bikes = new ArrayList<>();

	/**
	 * ユーザーの一意なID。
	 * データベースで登録時に自動生成されます。
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "ユーザーの一意なIDです。データベースで自動生成されます。")
	private int id;

	/**
	 * ユーザーのニックネームです。
	 * フロント側で表示させるためnullおよび空文字は入りません。
	 */
	@Column(name = "nickname", length = 50, nullable = false)
	@Size(max = 50)
	@NotBlank
	@Schema(description = "ユーザーのニックネーム。フロントエンドで表示されます。", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
	private String nickname;

	/**
	 * 外部認証から取得したメールアドレス（表示用のみ）。
	 * 認証には使用しません。
	 */
	@Column(name = "display_email")
	@Size(max = 255)
	@Email
	@Schema(description = "外部認証から取得したメールアドレス。表示用のみで認証には使用しません。", maxLength = 255)
	private String displayEmail;

	/**
	 * 外部認証システムから取得した一意なユーザーID。
	 * Google認証の場合はsubクレーム、Line認証の場合はLine IDなどを格納します。
	 * このフィールドがJWT認証の主キーとして機能します。
	 */
	@Column(name = "unique_user_id", unique = true, nullable = false)
	@NotBlank
	@Schema(description = "外部認証システムから取得した一意なユーザーID。JWT認証の主キーとして機能します。", requiredMode = Schema.RequiredMode.REQUIRED)
	private String uniqueUserId;

	/**
	 * ユーザーの権限情報。
	 * デフォルトは一般ユーザー（USER）です。
	 */
	@Column(name = "roles")
	@Builder.Default
	@Schema(description = "ユーザーの権限情報。デフォルトは'USER'です。")
	private String roles = "USER";

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
	 * ユーザー情報を更新します。
	 * 外部認証のため、ニックネームのみ更新可能です。
	 *
	 * @param request 更新リクエスト
	 */
	public void updateFrom(UserUpdateRequest request) {
		if(request.getNickname() != null) {
			this.nickname = request.getNickname();
		}
	}

	/**
	 * 論理削除のためのメソッドです。
	 * 日時はDBで自動設定されるためシステム側では日時の更新は行いません。
	 */
	public void softDelete() {

		this.isDeleted = true;
	}

	/**
	 * 論理削除されたユーザーを復元するメソッド名です。
	 * 日時はDBで自動設定されるためシステム側では日時の更新は行いません。
	 */
	public void restoreUser() {

		this.isDeleted = false;
	}
}