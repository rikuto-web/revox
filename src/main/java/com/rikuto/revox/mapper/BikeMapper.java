package com.rikuto.revox.mapper;

import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.domain.Bike;
import com.rikuto.revox.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bikeエンティティと関連するDTO間のマッピングおよび更新処理を行うクラスです。
 * ドメインの振る舞い（更新・削除などのロジック）は保持せず、純粋なデータ変換に専念します。
 */
@Component
public class BikeMapper {

	/**
	 * 単一のBikeエンティティをBikeResponse DTOに変換します。
	 *
	 * @param bike 変換する Bikeエンティティ。
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
	 * ユーザーへレスポンスする内容へ変換します。
	 * バイク情報をList化します
	 *
	 * @param bikeList ユーザーが保有するバイク
	 * @return バイクリスト
	 */
	public List<BikeResponse> toResponseList(List<Bike> bikeList) {
		return bikeList.stream()
				.map(this::toResponse)
				.toList();
	}

	/**
	 * BikeCreateRequest DTOと Userエンティティから新しい Bikeエンティティを作成します。
	 *
	 * @param request バイク作成リクエストを含む BikeCreateRequest DTO。
	 * @param user    バイクを所有する Userエンティティ。
	 * @return 作成された Bikeエンティティ。
	 */
	public Bike toEntity(BikeCreateRequest request, User user) {
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