package com.rikuto.revox.service;

import com.rikuto.revox.domain.User;
import com.rikuto.revox.mapper.BikeMapper;
import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.domain.Bike;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.repository.BikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * バイクに関するビジネスロジックを処理するサービスクラスです。
 */
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

	/**
	 * ユーザーIDに紐づいた全てのバイク情報を検索します。
	 *
	 * @param userId ユーザーID
	 * @return レスポンスへ変換後のバイクリスト
	 */
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
	public BikeResponse findByIdAndUserId (Integer userId, Integer bikeId){
		Bike bike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(userId, bikeId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"ユーザーID " + userId + " に紐づくバイクID " + bikeId + " が見つかりません。"));

		return bikeMapper.toResponse(bike);
	}

	/**
	 * 新しいバイク情報を登録します。
	 *
	 * @param request 登録するバイク情報を含むリクエストDTO
	 * @return 登録されたバイク情報
	 * @throws ResourceNotFoundException 指定されたユーザーが見つからない場合
	 */
	@Transactional
	public BikeResponse registerBike(BikeCreateRequest request) {
		User user = userService.findById(request.getUserId());

		Bike bike = bikeMapper.toEntity(user, request);

		Bike savedBike = bikeRepository.save(bike);

		return bikeMapper.toResponse(savedBike);
	}

	/**
	 * 既存のバイク情報を更新します。
	 *
	 * @param bikeId 更新するバイクのID
	 * @param updatedBikeRequest 更新されたバイク情報を含むリクエストDTO
	 * @return 更新されたバイク情報
	 * @throws ResourceNotFoundException 指定されたバイクが見つからない場合
	 */
	@Transactional
	public BikeResponse updateBike(BikeCreateRequest updatedBikeRequest, Integer bikeId) {
		Bike existingBike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(updatedBikeRequest.getUserId(), bikeId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"ユーザーID " + updatedBikeRequest.getUserId() + " に紐づくバイクID " + bikeId + " が見つかりません。"));

		existingBike.updateFrom(updatedBikeRequest);

		Bike savedBike = bikeRepository.save(existingBike);

		return bikeMapper.toResponse(savedBike);
	}


	/**
	 * 登録されているバイクを論理削除します。
	 *
	 * @param bikeId 更新するバイクID
	 * @throws ResourceNotFoundException 指定されたバイクが見つからない場合
	 */
	@Transactional
	public void softDeleteBike(Integer userId, Integer bikeId) {
		Bike existingBike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(userId, bikeId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"ユーザー ID " + userId + " に紐づくバイクID " + bikeId + "が見つかりません。"));

		existingBike.softDelete();

		bikeRepository.save(existingBike);
	}
}