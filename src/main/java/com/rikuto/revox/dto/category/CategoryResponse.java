package com.rikuto.revox.dto.category;

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
public class CategoryResponse {

	private int id;

	private String name;
	private Integer displayOrder;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
