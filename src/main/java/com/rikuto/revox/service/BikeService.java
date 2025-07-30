package com.rikuto.revox.service;

import com.rikuto.revox.converter.BikeConverter;
import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.entity.Bike;
import com.rikuto.revox.entity.User;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.repository.BikeRepository;
import com.rikuto.revox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BikeService {

	private final BikeRepository bikeRepository;
	private final UserRepository userRepository;
	private final BikeConverter bikeConverter;

	public BikeService(UserRepository userRepository, BikeRepository bikeRepository, BikeConverter bikeConverter) {
		this.userRepository = userRepository;
		this.bikeRepository = bikeRepository;
		this.bikeConverter = bikeConverter;
	}

	/**
	 * ユーザーIDに紐づいたバイク情報の検索をします。
	 * @param userId ユーザーID
	 * @return 登録されたバイク情報（BikeResponse）
	 * @throws ResourceNotFoundException 指定されたユーザーが見つからない場合
	 */
	public BikeResponse findBikeByUserId(Integer userId) {
		Bike bike = bikeRepository.findByUserIdAndIsDeletedFalse(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User ID " + userId + " に紐づくバイクが見つかりません。"));
		return bikeConverter.convertToBikeResponse(bike);
	}

	/**
	 * 新しいバイク情報を登録します。
	 * @param request 登録するバイク情報を含むリクエストDTO
	 * @return 登録されたバイク情報（BikeResponse）
	 * @throws ResourceNotFoundException 指定されたユーザーが見つからない場合
	 */
	@Transactional
	public BikeResponse registerBike(BikeCreateRequest request) {
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーID " + request.getUserId() + " が見つかりません。"));

		Bike bike = bikeConverter.convertToBikeEntity(request, user);

		Bike savedBike = bikeRepository.save(bike);
		return bikeConverter.convertToBikeResponse(savedBike);
	}

	/**
	 * 既存のバイク情報を更新します。
	 * @param bikeId 更新するバイクのID
	 * @param updatedBikeRequest 更新されたバイク情報を含むリクエストDTO
	 * @return 更新されたバイク情報（BikeResponse）
	 * @throws ResourceNotFoundException 指定されたバイクが見つからない場合
	 */
	@Transactional
	public BikeResponse updateBike(Integer bikeId, BikeCreateRequest updatedBikeRequest) {
		Bike existingBike = bikeRepository.findById(bikeId)
				.orElseThrow(() -> new ResourceNotFoundException("バイクID " + bikeId + " が見つかりません。"));

		Bike updated = bikeConverter.updateBikeEntityFromDto(updatedBikeRequest, existingBike);

		Bike savedBike = bikeRepository.save(updated);
		return bikeConverter.convertToBikeResponse(savedBike);
	}

	/**
	 * 登録されているバイクを論理削除します。
	 * @param bikeId 更新するバイクID
	 * @throws ResourceNotFoundException 指定されたバイクが見つからない場合
	 */
	@Transactional
	public void softDeleteBike(Integer bikeId) {
		Bike existingBike = bikeRepository.findById(bikeId)
				.orElseThrow(() -> new ResourceNotFoundException("バイクID " + bikeId + " が見つかりません。"));

		existingBike.setDeleted(true);
		existingBike.setUpdatedAt(LocalDateTime.now());
		bikeRepository.save(existingBike);
	}
}