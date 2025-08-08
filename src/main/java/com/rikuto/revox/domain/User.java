package com.rikuto.revox.domain;

import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.dto.user.UserUpdateRequest;
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
	private List<Bike> bikes = new ArrayList<>();

	/**
	 * ユーザーの一意なID。
	 * データベースで登録時に自動生成されます。
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	/**
	 * ユーザーのニックネームです。
	 * フロント側で表示させるためnullおよび空文字は入りません。
	 */
	@Column(name = "nickname", length = 50, nullable = false)
	@Size(max = 50)
	@NotBlank
	private String nickname;

	/**
	 * 外部認証から取得したメールアドレス（表示用のみ）。
	 * 認証には使用しません。
	 */
	@Column(name = "display_email")
	@Size(max = 255)
	@Email
	private String displayEmail;

	/**
	 * 外部認証システムから取得した一意なユーザーID。
	 * Google認証の場合はsubクレーム、Line認証の場合はLine IDなどを格納します。
	 * このフィールドがJWT認証の主キーとして機能します。
	 */
	@Column(name = "unique_user_id", unique = true, nullable = false)
	@NotBlank
	private String uniqueUserId;

	/**
	 * ユーザーの権限情報。
	 * デフォルトは一般ユーザー（USER）です。
	 */
	@Column(name = "roles")
	@Builder.Default
	private String roles = "USER";

	/**
	 * 論理削除フラグ。
	 * trueの場合、レコードは削除済みとして扱われます。
	 */
	@Column(name = "is_deleted", nullable = false)
	@Builder.Default
	private boolean isDeleted = false;

	/**
	 * レコードが作成された日時
	 * 日時はDBで自動設定されるためシステム側では日時の更新は行いません。
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
	 * ユーザー情報を更新します。
	 * 外部認証のため、ニックネームのみ更新可能です。
	 *
	 * @param request 更新リクエスト
	 */
	public void updateFrom(UserUpdateRequest request) {

		if(request.getNickname() != null && !request.getNickname().isEmpty()) {

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
}