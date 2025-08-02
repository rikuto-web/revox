package com.rikuto.revox.domain;

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
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ユーザ情報を表すドメインです。
 * データベースのusersテーブルにマッピングされています。
 * １人のユーザーは複数のバイクを保持することができます。
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

	/**
	 * このユーザーが所有するバイクのリストです。
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
	 * ユーザー認証に使用するメールアドレスです。
	 * バリデーションで不正なデータを受け付けません。
	 * 他認証システムがあるためnullを許容しています。
	 */
	@Column(name = "email")
	@Size(max = 255)
	@Email
	private String email;

	/**
	 * Google認証時に取得されるGoogleユーザーID。ユニーク制約があります。
	 * 他認証方法があるためデフォルトでNullを許容しています。
	 */
	@Column(name = "google_id", length = 100, unique = true)
	@Size(max = 100)
	private String googleId;

	/**
	 * Line認証時に取得されるLineユーザーID。ユニーク制約があります。
	 * 他認証方法があるためデフォルトでNullを許容しています。
	 */
	@Column(name = "line_id", length = 100, unique = true)
	@Size(max = 100)
	private String lineId;

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
	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	/**
	 * レコードが更新された最終日時
	 */
	@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;
}
