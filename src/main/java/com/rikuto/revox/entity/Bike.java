package com.rikuto.revox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bikes")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Bike {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "manufacturer", length = 50, nullable = false)
	private String manufacturer;

	@Column(name = "model_name", length = 100, nullable = false)
	private String modelName;

	@Column(name = "model_code", length = 20, nullable = true)
	private String modelCode;

	@Column(name = "model_year")
	private int modelYear;

	@Column(name = "current_mileage")
	private int currentMileage;

	@Column(name = "purchase_date")
	private LocalDate purchaseDate;

	@Column(name = "image_url", length = 2048, nullable = true)
	private String imageUrl;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}
