package com.rikuto.revox.dto.bike;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeResponse {
	private Integer id;
	private String manufacturer;
	private String modelName;
	private String modelCode;
	private Integer modelYear;
	private Integer currentMileage;
	private LocalDate purchaseDate;
	private String imageUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private Integer userId;
}