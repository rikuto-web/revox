package com.rikuto.revox.dto.maintenancetask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内部処理を行った後ユーザーへ返すレスポンス内容のDTOです。
 *
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceTaskResponse {

	private Integer id;
	private String name;
	private String description;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private Integer categoryId;
}
