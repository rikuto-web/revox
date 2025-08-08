package com.rikuto.revox.dto.maintenancetask;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MaintenanceTaskUpdateRequest {

	@NotBlank
	@Size(max = 100)
	private String name;

	@NotBlank
	@Size(max = 5000)
	private String description;
}
