package com.rikuto.revox.controller;

import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.dto.bike.BikeUpdateRequest;
import com.rikuto.revox.service.BikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "バイク情報に関する管理", description = "ユーザーが所有するバイク情報の登録、取得、更新、削除を管理するエンドポイント群です。")
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
	 */
	@Operation(summary = "バイク情報を新規登録する", description = "指定されたユーザーに新しいバイク情報を登録します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "バイク情報の登録に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = BikeResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（バリデーションエラーなど）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない")
	})
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<BikeResponse> registerBike(
			@RequestBody @Valid BikeCreateRequest request,
			@Parameter(description = "バイク情報を登録するユーザーの一意の識別子。", required = true)
			@PathVariable @Positive Integer userId
	) {
		BikeResponse registerBike = bikeService.registerBike(request, userId);
		return new ResponseEntity<>(registerBike, HttpStatus.CREATED);
	}

	// READ
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 指定されたユーザーIDに紐づく全てのバイク情報を取得します。
	 */
	@Operation(summary = "ユーザーのバイク情報リストを取得する", description = "指定されたユーザーIDに紐づく全てのバイク情報をリスト形式で取得します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "バイク情報の取得に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = BikeResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（ユーザーIDが不正など）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない"),
			@ApiResponse(responseCode = "404", description = "ユーザーが見つからない")
	})
	@GetMapping
	@PreAuthorize("hasAnyRole('GUEST', 'USER')")
	public ResponseEntity<List<BikeResponse>> getBikeListByUserId(
			@Parameter(description = "バイク情報リストを取得したいユーザーの一意の識別子。", required = true)
			@PathVariable @Positive Integer userId
	) {
		List<BikeResponse> bikeResponseList = bikeService.findBikeByUserId(userId);
		return ResponseEntity.ok(bikeResponseList);
	}

	/**
	 * 指定されたユーザーIDに紐づく特定のバイク情報を取得します。
	 */
	@Operation(summary = "特定のバイク情報を取得する", description = "指定されたユーザーが所有する特定のバイク情報を取得します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "バイク情報の取得に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = BikeResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（IDが不正など）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない"),
			@ApiResponse(responseCode = "404", description = "バイク情報が見つからない")
	})
	@GetMapping("/bike/{bikeId}")
	@PreAuthorize("hasAnyRole('GUEST', 'USER')")
	public ResponseEntity<BikeResponse> getBikeByUserIdAndBikeId(
			@Parameter(description = "取得したいバイクの一意の識別子。", required = true)
			@PathVariable("bikeId") @Positive Integer bikeId,
			@Parameter(description = "バイクを所有するユーザーの一意の識別子。", required = true)
			@PathVariable("userId") @Positive Integer userId
	) {
		BikeResponse bikeResponse = bikeService.findByIdAndUserId(bikeId, userId);
		return ResponseEntity.ok(bikeResponse);
	}

	// UPDATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 既存のバイク情報を、受け取ったリクエスト内容に更新します。
	 */
	@Operation(summary = "バイク情報を更新する", description = "既存のバイク情報を部分的に更新します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "バイク情報の更新に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = BikeResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（バリデーションエラーなど）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない"),
			@ApiResponse(responseCode = "404", description = "バイク情報が見つからない")
	})
	@PatchMapping("/bike/{bikeId}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<BikeResponse> updateBike(
			@RequestBody @Valid BikeUpdateRequest request,
			@Parameter(description = "更新したいバイクの一意の識別子。", required = true)
			@PathVariable("bikeId") @Positive Integer bikeId,
			@Parameter(description = "バイクを所有するユーザーの一意の識別子。", required = true)
			@PathVariable("userId") @Positive Integer userId
	) {
		BikeResponse updateBike = bikeService.updateBike(request, bikeId, userId);
		return ResponseEntity.ok(updateBike);
	}

	// DELETE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * ユーザーIDに紐づく特定のバイク情報を論理削除します。
	 */
	@Operation(summary = "バイク情報を論理削除する", description = "特定のバイク情報をデータベースから物理的に削除するのではなく、論理的に削除（非表示）にします。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "バイク情報の論理削除に成功"),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（IDが不正など）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない"),
			@ApiResponse(responseCode = "404", description = "バイク情報が見つからない")
	})
	@PatchMapping("/bike/{bikeId}/softDelete")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Void> softDeleteBike(
			@Parameter(description = "論理削除したいバイクの一意の識別子。", required = true)
			@PathVariable("bikeId") @Positive Integer bikeId,
			@Parameter(description = "バイクを所有するユーザーの一意の識別子。", required = true)
			@PathVariable("userId") @Positive Integer userId
	) {
		bikeService.softDeleteBike(bikeId, userId);
		return ResponseEntity.noContent().build();
	}
}