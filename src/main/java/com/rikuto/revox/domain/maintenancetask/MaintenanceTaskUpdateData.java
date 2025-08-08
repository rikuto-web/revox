package com.rikuto.revox.domain.maintenancetask;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class MaintenanceTaskUpdateData {
	private String name;
	private String description;
}