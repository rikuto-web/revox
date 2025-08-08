package com.rikuto.revox.dto.bike;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class BikeUpdateRequest {

	@Size(max = 50)
	@NotBlank
	private String manufacturer;

	@Size(max = 100)
	@NotBlank
	private String modelName;

	@Size(max = 20)
	private String modelCode;

	private Integer modelYear;

	private Integer currentMileage;

	private LocalDate purchaseDate;

	@Size(max = 2048)
	private String imageUrl;
}