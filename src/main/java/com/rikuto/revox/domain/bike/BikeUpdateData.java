package com.rikuto.revox.domain.bike;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class BikeUpdateData {
	private final String manufacturer;
	private final String modelName;
	private final String modelCode;
	private final Integer modelYear;
	private final Integer currentMileage;
	private final LocalDate purchaseDate;
	private final String imageUrl;

	public BikeUpdateData(String manufacturer, String modelName, String modelCode, Integer modelYear, Integer currentMileage, LocalDate purchaseDate, String imageUrl) {
		this.manufacturer = manufacturer;
		this.modelName = modelName;
		this.modelCode = modelCode;
		this.modelYear = modelYear;
		this.currentMileage = currentMileage;
		this.purchaseDate = purchaseDate;
		this.imageUrl = imageUrl;
	}
}