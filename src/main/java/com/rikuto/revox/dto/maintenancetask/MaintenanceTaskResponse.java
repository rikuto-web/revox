package com.rikuto.revox.dto.maintenancetask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceTaskResponse {

	private Integer id;
	private Integer categoryId;
	private String categoryName; // カテゴリー名も含める
	private String name;
	private String description;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
