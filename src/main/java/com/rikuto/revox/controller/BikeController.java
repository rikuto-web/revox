package com.rikuto.revox.controller;

import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.dto.bike.BikeUpdateRequest;
import com.rikuto.revox.service.BikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * バイク情報に関するコントローラーです。
 */
@RestController
@RequestMapping("/api/bikes/user/{userId}")
public class BikeController {

	private final BikeService bikeService;

	public BikeController(BikeService bikeService) {
		this.bikeService = bikeService;
	}

	// CREATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 新しいバイク情報を登録します。
	 * POST /api/bikes/user/{userId}
	 *
	 * @param request 登録するバイク情報を含むリクエストDTO
	 * @return 登録されたバイク情報とHTTPステータス201 Created
	 */
	@PostMapping
	public ResponseEntity<BikeResponse> registerBike(@RequestBody @Valid BikeCreateRequest request,
	                                                 @PathVariable @Positive Integer userId) {
		BikeResponse registerBike = bikeService.registerBike(request, userId);

		return new ResponseEntity<>(registerBike, HttpStatus.CREATED);
	}

	// READ
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 指定されたユーザーIDに紐づく全てのバイク情報を取得します。
	 * GET /api/bikes/user/{userId}
	 *
	 * @param userId ユーザーID
	 * @return ユーザーが保有する全てのバイク情報とHTTPステータス200 OK
	 */
	@GetMapping
	public ResponseEntity<List<BikeResponse>> getBikeListByUserId(@PathVariable @Positive Integer userId) {
		List<BikeResponse> bikeResponseList = bikeService.findBikeByUserId(userId);

		return ResponseEntity.ok(bikeResponseList);
	}

	/**
	 * 指定されたユーザーIDに紐づく特定のバイク情報を取得します。
	 * GET /api/bikes/user/{userId}/bike/{bikeId}
	 *
	 * @param userId ユーザーID
	 * @param bikeId 特定のバイクID
	 * @return 検索されたバイク情報とHTTPステータス200 OK
	 */
	@GetMapping("/bike/{bikeId}")
	public ResponseEntity<BikeResponse> getBikeByUserIdAndBikeId(@PathVariable("bikeId") @Positive Integer bikeId,
	                                                             @PathVariable("userId") @Positive Integer userId) {
		BikeResponse bikeResponse = bikeService.findByIdAndUserId(bikeId, userId);

		return ResponseEntity.ok(bikeResponse);
	}

	// UPDATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 既存のバイク情報を、受け取ったリクエスト内容に更新します。
	 * PATCH /api//bikes/user/{userId}/bike/{bikeId}
	 *
	 * @param bikeId  更新するバイクID
	 * @param request 更新されたバイク情報を含むリクエストDTO
	 * @return 更新されたバイク情報とHTTPステータス200 OK
	 */
	@PatchMapping("/bike/{bikeId}")
	public ResponseEntity<BikeResponse> updateBike(@RequestBody @Valid BikeUpdateRequest request,
	                                               @PathVariable("bikeId") @Positive Integer bikeId,
	                                               @PathVariable("userId") @Positive Integer userId) {
		BikeResponse updateBike = bikeService.updateBike(request, bikeId, userId);

		return ResponseEntity.ok(updateBike);
	}

	// DELETE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * ユーザーIDに紐づく特定のバイク情報を論理削除します。
	 * PATCH /api/bikes/user/{userId}/bike/{bikeId}/softDelete
	 *
	 * @param userId ユーザーID
	 * @param bikeId 論理削除するバイクID
	 * @return HTTPステータス204 No Content
	 */
	@PatchMapping("/bike/{bikeId}/softDelete")
	public ResponseEntity<Void> softDeleteBike(@PathVariable("bikeId") @Positive Integer bikeId,
	                                           @PathVariable("userId") @Positive Integer userId) {
		bikeService.softDeleteBike(bikeId, userId);

		return ResponseEntity.noContent().build();
	}
}