package com.rikuto.revox.controller;

import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.dto.bike.BikeUpdateRequest;
import com.rikuto.revox.service.BikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * バイク情報のCRUD操作を扱うコントローラーです。
 */
@RestController
@RequestMapping("/api/bikes/user/{userId}")
public class BikeController {

	private final BikeService bikeService;

	public BikeController(BikeService bikeService) {
		this.bikeService = bikeService;
	}

	/**
	 * 指定されたユーザーIDに紐づく全てのバイク情報を取得します。
	 * GET /api/bikes/user/{userId}
	 *
	 * @param userId バイクを検索するユーザーのID
	 * @return ユーザーが保有する全てのバイク情報
	 */
	@GetMapping
	public ResponseEntity<List<BikeResponse>> getBikeListByUserId (@PathVariable @Positive Integer userId){
		List<BikeResponse> bikeResponseList = bikeService.findBikeByUserId(userId);

		return ResponseEntity.ok(bikeResponseList);
	}

	/**
	 * 指定されたユーザーIDに紐づく特定のバイク情報を取得します。
	 * GET /api/bikes/user/{userId}/bike/{bikeId}
	 *
	 * @param userId バイクを検索するユーザーのID
	 * @param bikeId ユーザーが保有する特定のバイクID
	 * @return 検索されたバイク情報とHTTPステータス200 OK
	 */
	@GetMapping("/bike/{bikeId}")
	public ResponseEntity<BikeResponse> getBikeByUserIdAndBikeId(@PathVariable @Positive Integer userId,
	                                                             @PathVariable @Positive Integer bikeId) {
		BikeResponse bikeResponse = bikeService.findByIdAndUserId(userId, bikeId);

		return ResponseEntity.ok(bikeResponse);
	}

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

	/**
	 * 既存のバイク情報を、受け取ったリクエスト内容に更新します。
	 * PUT /api/user/{userId}/bike/{bikeId}
	 *
	 * @param bikeId 更新するバイクのID
	 * @param request 更新されたバイク情報を含むリクエストDTO
	 * @return 更新されたバイク情報（BikeResponse）とHTTPステータス200 OK
	 */
	@PutMapping("/bike/{bikeId}")
	public ResponseEntity<BikeResponse> updateBike(@RequestBody @Valid BikeUpdateRequest request,
	                                               @PathVariable @Positive Integer bikeId,
	                                               @PathVariable @Positive Integer userId) {
		BikeResponse updateBike = bikeService.updateBike(request, bikeId, userId);

		return ResponseEntity.ok(updateBike);
	}

	/**
	 * ユーザーIDに紐づく特定のバイク情報を論理削除します。
	 * PATCH /api/bikes/user/{userId}/bike/{bikeId}
	 *
	 * @param userId 削除するバイクを保有しているユーザーID
	 * @param bikeId 論理削除するバイクのID
	 * @return HTTPステータス204 No Content
	 */
	@PatchMapping("/bike/{bikeId}")
	public ResponseEntity<Void> softDeleteBike(@PathVariable @Positive Integer userId,
	                                           @PathVariable @Positive Integer bikeId) {
		bikeService.softDeleteBike(userId, bikeId);

		return ResponseEntity.noContent().build();
	}
}