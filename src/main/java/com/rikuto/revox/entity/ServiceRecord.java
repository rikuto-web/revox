package com.rikuto.revox.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_records")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ServiceRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "bike_id", nullable = false)
	private Bike bike;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "maintenance_task_id", nullable = false)
	private MaintenanceTask maintenanceTask;

	@Column(name = "ai_advice_original", columnDefinition = "TEXT", nullable = false)
	private String aiAdviceOriginal;

	@Column(name = "user_edited_content", columnDefinition = "TEXT", nullable = false)
	private String userEditedContent;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}

