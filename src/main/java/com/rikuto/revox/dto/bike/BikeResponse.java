package com.rikuto.revox.dto.bike;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
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
public class BikeResponse {

	private Integer id;
	private Integer userId;

	private String manufacturer;
	private String modelName;
	private String modelCode;
	private Integer modelYear;
	private Integer currentMileage;
	private LocalDate purchaseDate;
	private String imageUrl;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}