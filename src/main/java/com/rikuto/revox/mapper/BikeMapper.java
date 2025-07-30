package com.rikuto.revox.mapper;

import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.entity.Bike;
import com.rikuto.revox.entity.User;
import com.rikuto.revox.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Bikeエンティティと関連するDTO間のマッピングおよび更新処理を行うクラスです。
 * 主に、エンティティからレスポンスDTOへの変換、リクエストDTOからエンティティへの作成、
 * および既存のエンティティの更新ロジックを提供します。
 */
@Component
public class BikeMapper {

	/**
	 * BikeエンティティをBikeResponse DTOに変換します。
	 * 変換対象のバイクが見つからない場合は ResourceNotFoundException をスローします。
	 *
	 * @param bike 変換する Bikeエンティティ。nullであってはなりません。
	 * @return 変換された BikeResponse DTO。
	 * @throws ResourceNotFoundException 引数 bike が null の場合。
	 */
	public BikeResponse toResponse(Bike bike) {
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
	 * BikeCreateRequest DTOと Userエンティティから新しい Bikeエンティティを作成します。
	 *
	 * @param request バイク作成リクエストを含む BikeCreateRequest DTO。
	 * @param user バイクを所有する Userエンティティ。
	 * @return 作成された Bikeエンティティ。
	 */
	public Bike toEntity(BikeCreateRequest request, User user) {
		return new Bike(
				user,
				request.getManufacturer(),
				request.getModelName(),
				request.getModelCode(),
				request.getModelYear(),
				request.getCurrentMileage(),
				request.getPurchaseDate(),
				request.getImageUrl()
		);
	}

	/**
	 * BikeCreateRequest DTOのデータを使用して、既存の Bikeエンティティの状態を更新（論理削除）します。
	 * 実際にエンティティの更新（論理削除）を行うのは existingBike オブジェクトの updateFrom メソッドです。
	 *
	 * @param request 更新データを含む BikeCreateRequest DTO。このメソッドでは主に論理削除のトリガーとして使用される場合があります。
	 * @param existingBike 更新対象の既存の Bikeエンティティ。
	 */
	public void updateEntityFromDto(BikeCreateRequest request, Bike existingBike) {
		existingBike.updateFrom(request);
	}
}