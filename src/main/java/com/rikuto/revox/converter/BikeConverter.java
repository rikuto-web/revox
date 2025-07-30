package com.rikuto.revox.converter;

import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.entity.Bike;
import com.rikuto.revox.entity.User;
import com.rikuto.revox.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BikeConverter {

	/**
	 * BikeエンティティをBikeResponse DTOに変換します。
	 * @param bike 変換元のBikeエンティティ
	 * @return 変換されたBikeResponse DTO
	 * @throws ResourceNotFoundException 指定されたバイクエンティティがnullの場合
	 */
	public BikeResponse convertToBikeResponse(Bike bike) {
		if (bike == null) {
			throw new ResourceNotFoundException("変換対象のバイクが見つかりません。");
		}
		return BikeResponse.builder()
				.id(bike.getId())
				.manufacturer(bike.getManufacturer())
				.modelName(bike.getModelName())
				.modelCode(bike.getModelCode())
				.modelYear(bike.getModelYear())
				.currentMileage(bike.getCurrentMileage())
				.purchaseDate(bike.getPurchaseDate())
				.imageUrl(bike.getImageUrl())
				.createdAt(bike.getCreatedAt())
				.updatedAt(bike.getUpdatedAt())
				.userId(bike.getUser() != null ? bike.getUser().getId() : null)
				.build();
	}

	/**
	 * BikeCreateRequest DTOとUserエンティティからBikeエンティティを生成します。
	 * 新規登録時の初期設定（createdAt, updatedAt, isDeleted）も行います。
	 * @param request 変換元のBikeCreateRequest DTO
	 * @param user バイクの所有者となるUserエンティティ
	 * @return 変換されたBikeエンティティ
	 */
	public Bike convertToBikeEntity(BikeCreateRequest request, User user) {
		if (request == null) {
			throw new IllegalArgumentException("バイク作成リクエストがnullです。");
		}
		Bike bike = new Bike();
		bike.setUser(user);
		bike.setManufacturer(request.getManufacturer());
		bike.setModelName(request.getModelName());
		bike.setModelCode(request.getModelCode());
		bike.setModelYear(request.getModelYear());
		bike.setCurrentMileage(request.getCurrentMileage());
		bike.setPurchaseDate(request.getPurchaseDate());
		bike.setImageUrl(request.getImageUrl());

		bike.setCreatedAt(LocalDateTime.now());
		bike.setUpdatedAt(LocalDateTime.now());
		bike.setDeleted(false);

		return bike;
	}

	/**
	 * BikeCreateRequest DTOの情報を既存のBikeエンティティにマッピングします。
	 * 主に更新処理で使用します。
	 * @param request 更新情報を含むBikeCreateRequest DTO
	 * @param existingBike 更新対象の既存Bikeエンティティ
	 * @return 更新されたBikeエンティティ
	 * @throws IllegalArgumentException リクエストまたは既存バイクエンティティがnullの場合
	 */
	public Bike updateBikeEntityFromDto(BikeCreateRequest request, Bike existingBike) {
		if (request == null || existingBike == null) {
			throw new IllegalArgumentException("更新リクエストまたは既存のバイクエンティティがnullです。");
		}
		existingBike.setManufacturer(request.getManufacturer());
		existingBike.setModelName(request.getModelName());
		existingBike.setModelCode(request.getModelCode());
		existingBike.setModelYear(request.getModelYear());
		existingBike.setCurrentMileage(request.getCurrentMileage());
		existingBike.setPurchaseDate(request.getPurchaseDate());
		existingBike.setImageUrl(request.getImageUrl());

		existingBike.setUpdatedAt(LocalDateTime.now());

		return existingBike;
	}
}