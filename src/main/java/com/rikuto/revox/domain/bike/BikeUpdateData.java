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
}