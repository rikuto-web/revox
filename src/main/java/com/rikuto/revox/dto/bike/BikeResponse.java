package com.rikuto.revox.dto.bike;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * バイク情報に対するレスポンス内容のDTOです。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "ユーザーが所有するバイクの詳細情報を表すレスポンスDTOです。")
public class BikeResponse {

	@Schema(description = "バイクの一意なID。")
	private Integer id;

	@Schema(description = "バイクを所有するユーザーの一意なID。")
	private Integer userId;

	@Schema(description = "バイクのメーカー名。")
	private String manufacturer;

	@Schema(description = "バイクの車両名。")
	private String modelName;

	@Schema(description = "バイクの型式。")
	private String modelCode;

	@Schema(description = "バイクの年式。")
	private Integer modelYear;

	@Schema(description = "現在の走行距離（km）。")
	private Integer currentMileage;

	@Schema(description = "購入日。")
	private LocalDate purchaseDate;

	@Schema(description = "バイクの画像URL。")
	private String imageUrl;

	@Schema(description = "レコードが作成された日時。")
	private LocalDateTime createdAt;

	@Schema(description = "レコードが更新された最終日時。")
	private LocalDateTime updatedAt;
}