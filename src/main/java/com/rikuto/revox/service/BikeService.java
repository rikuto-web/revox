package com.rikuto.revox.service;

import com.rikuto.revox.domain.Bike;
import com.rikuto.revox.domain.User;
import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.dto.bike.BikeUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.BikeMapper;
import com.rikuto.revox.repository.BikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * バイクに関するビジネスロジックを処理するサービスクラスです。
 */
@Slf4j
@Service
public class BikeService {

	private final BikeRepository bikeRepository;

	private final UserService userService;

	private final BikeMapper bikeMapper;

	public BikeService(UserService userService,
	                   BikeRepository bikeRepository,
	                   BikeMapper bikeMapper) {
		this.userService = userService;
		this.bikeRepository = bikeRepository;
		this.bikeMapper = bikeMapper;
	}

	// CREATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 新しいバイク情報を登録します。
	 *
	 * @param request 登録するバイク情報を含むリクエストDTO
	 * @return 登録されたバイク情報
	 * @throws ResourceNotFoundException 指定されたユーザーが見つからない場合
	 */
	@Transactional
	public BikeResponse registerBike(BikeCreateRequest request, Integer userId) {
		log.info("新しいバイクの登録を開始します。");
		User user = userService.findById(userId);
		Bike bike = bikeMapper.toDomain(user, request);
		Bike savedBike = bikeRepository.save(bike);

		log.info("新しいバイクが正常に登録されました。");
		return bikeMapper.toResponse(savedBike);
	}

	// READ
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * ユーザーIDに紐づいた全てのバイク情報を検索します。
	 *
	 * @param userId ユーザーID
	 * @return レスポンスへ変換後のバイクリスト
	 */
	@Transactional(readOnly = true)
	public List<BikeResponse> findBikeByUserId(Integer userId) {
		List<Bike> bikeList = bikeRepository.findByUserIdAndIsDeletedFalse(userId);

		return bikeMapper.toResponseList(bikeList);
	}

	/**
	 * ユーザーが保有する特定のバイクを検索します。
	 * 該当するバイクが見つからない場合は例外をスローします。
	 *
	 * @param userId ユーザーID
	 * @param bikeId ユーザーが保有する特定のバイクID
	 * @return レスポンスへ変換後のバイク情報
	 */
	@Transactional(readOnly = true)
	public BikeResponse findByIdAndUserId(Integer bikeId, Integer userId) {
		Bike bike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(bikeId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーID " + userId + " に紐づくバイクID " + bikeId + " が見つかりません。"));

		return bikeMapper.toResponse(bike);
	}

	// UPDATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 既存のバイク情報を更新します。
	 *
	 * @param bikeId  更新するバイクのID
	 * @param request 更新されたバイク情報を含むリクエストDTO
	 * @return 更新されたバイク情報
	 * @throws ResourceNotFoundException 指定されたバイクが見つからない場合
	 */
	@Transactional
	public BikeResponse updateBike(BikeUpdateRequest request, Integer bikeId, Integer userId) {
		log.info("バイク情報の更新を開始します。");
		Bike existingBike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(bikeId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーID " + userId + " に紐づくバイクID " + bikeId + " が見つかりません。"));

		BikeUpdateRequest updateBikeData = BikeUpdateRequest.builder()
				.manufacturer(request.getManufacturer())
				.modelName(request.getModelName())
				.modelCode(request.getModelCode())
				.modelYear(request.getModelYear())
				.currentMileage(request.getCurrentMileage())
				.purchaseDate(request.getPurchaseDate())
				.imageUrl(request.getImageUrl())
				.build();

		existingBike.updateFrom(updateBikeData);

		Bike savedBike = bikeRepository.save(existingBike);

		log.warn("バイク情報が正常に更新されました。");
		return bikeMapper.toResponse(savedBike);
	}

	// DELETE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 登録されているバイクを論理削除します。
	 *
	 * @param bikeId 更新するバイクID
	 * @throws ResourceNotFoundException 指定されたバイクが見つからない場合
	 */
	@Transactional
	public void softDeleteBike(Integer bikeId, Integer userId) {
		Bike existingBike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(bikeId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザー ID " + userId + " に紐づくバイクID " + bikeId + "が見つかりません。"));

		existingBike.softDelete();

		bikeRepository.save(existingBike);
	}
}