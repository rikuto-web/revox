package com.rikuto.revox.controller;

import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
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
@RequestMapping("/api/bikes")
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
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<BikeResponse>> getBikeListByUserId (@PathVariable @Positive Integer userId){
		List<BikeResponse> bikeResponseList = bikeService.findBikeByUserId(userId);

		return ResponseEntity.ok(bikeResponseList);
	}

	/**
	 * 指定されたユーザーIDに紐づく特定のバイク情報を取得します。
	 * GET /api/bikes/user/{userId}/{bikeId}
	 *
	 * @param userId バイクを検索するユーザーのID
	 * @param bikeId ユーザーが保有する特定のバイクID
	 * @return 検索されたバイク情報とHTTPステータス200 OK
	 */
	@GetMapping("/user/{userId}/{bikeId}")
	public ResponseEntity<BikeResponse> getBikeByUserIdAndBikeId(@PathVariable @Positive Integer userId,
	                                                             @PathVariable @Positive Integer bikeId) {
		BikeResponse bikeResponse = bikeService.findByIdAndUserId(userId, bikeId);

		return ResponseEntity.ok(bikeResponse);
	}

	/**
	 * 新しいバイク情報を登録します。
	 * POST /api/bikes
	 *
	 * @param request 登録するバイク情報を含むリクエストDTO
	 * @return 登録されたバイク情報とHTTPステータス201 Created
	 */
	@PostMapping
	public ResponseEntity<BikeResponse> registerBike(@RequestBody @Valid BikeCreateRequest request) {
		BikeResponse registerBike = bikeService.registerBike(request);

		return new ResponseEntity<>(registerBike, HttpStatus.CREATED);
	}

	/**
	 * 既存のバイク情報を、受け取ったリクエスト内容に更新します。
	 * PUT /api/bikes/{bikeId}
	 *
	 * @param bikeId 更新するバイクのID
	 * @param request 更新されたバイク情報を含むリクエストDTO
	 * @return 更新されたバイク情報（BikeResponse）とHTTPステータス200 OK
	 */
	@PutMapping("/{bikeId}")
	public ResponseEntity<BikeResponse> updateBike(@PathVariable @Positive Integer bikeId,
	                                               @RequestBody @Valid BikeCreateRequest request) {
		BikeResponse updateBike = bikeService.updateBike(request, bikeId);

		return ResponseEntity.ok(updateBike);
	}

	/**
	 * ユーザーIDに紐づく特定のバイク情報を論理削除します。
	 * PATCH /api/bikes/{userId}/{bikeId}
	 *
	 * @param userId 削除するバイクを保有しているユーザーID
	 * @param bikeId 論理削除するバイクのID
	 * @return HTTPステータス204 No Content
	 */
	@PatchMapping("/{userId}/{bikeId}")
	public ResponseEntity<Void> softDeleteBike(@PathVariable @Positive Integer userId,
	                                           @PathVariable @Positive Integer bikeId) {
		bikeService.softDeleteBike(userId, bikeId);

		return ResponseEntity.noContent().build();
	}
}