package com.rikuto.revox.mapper;

import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.domain.Bike;
import com.rikuto.revox.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bikeドメインと関連するDTO間のマッピングおよび更新処理を行うクラスです。
 * ドメインの振る舞い（更新・削除などのロジック）は保持せず、純粋なデータ変換に専念します。
 */
@Component
public class BikeMapper {

	/**
	 * 単一のBikeドメインをBikeResponse DTOに変換します。
	 *
	 * @param bike 変換する Bikeドメイン。
	 * @return 変換された BikeResponse DTO。
	 */
	public BikeResponse toResponse(Bike bike) {
		return BikeResponse.builder()
				.id(bike.getId())
				.manufacturer(bike.getManufacturer())
				.modelName(bike.getModelName())
				.modelCode(bike.getModelCode())
				.modelYear(bike.getModelYear())
				.currentMileage(bike.getCurrentMileage())
				.purchaseDate(bike.getPurchaseDate())
				.imageUrl(bike.getImageUrl())

				.userId(bike.getUser() != null ? bike.getUser().getId() : null)
				.build();
	}

	/**
	 * BikeドメインのリストをBikeResponse DTOのリストに変換します。
	 *
	 * @param bikeList 変換対象のBikeドメインのリスト。
	 * @return 変換されたBikeResponse DTOのリスト。
	 */
	public List<BikeResponse> toResponseList(List<Bike> bikeList) {
		return bikeList.stream()
				.map(this::toResponse)
				.toList();
	}

	/**
	 * BikeCreateRequest DTOと Userドメインから新しい Bikeドメインを作成します。
	 *
	 * @param request バイク作成リクエストを含む BikeCreateRequest DTO。
	 * @param user    バイクを所有する Userドメイン。
	 * @return 作成された Bikeドメイン。
	 */
	public Bike toEntity(User user, BikeCreateRequest request) {
		return Bike.builder()
				.user(user)
				.manufacturer(request.getManufacturer())
				.modelName(request.getModelName())
				.modelCode(request.getModelCode())
				.modelYear(request.getModelYear())
				.currentMileage(request.getCurrentMileage())
				.purchaseDate(request.getPurchaseDate())
				.imageUrl(request.getImageUrl())
				.build();
	}
}