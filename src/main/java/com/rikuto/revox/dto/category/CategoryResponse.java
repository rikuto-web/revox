package com.rikuto.revox.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * カテゴリー情報に対するレスポンス内容のDTOです。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "カテゴリー情報に対するレスポンスDTOです。")
public class CategoryResponse {

	@Schema(description = "カテゴリーの一意なID。")
	private int id;

	@Schema(description = "カテゴリー名。")
	private String name;

	@Schema(description = "カテゴリーの表示順。")
	private Integer displayOrder;

	@Schema(description = "レコードが作成された日時。")
	private LocalDateTime createdAt;

	@Schema(description = "レコードが更新された最終日時。")
	private LocalDateTime updatedAt;
}