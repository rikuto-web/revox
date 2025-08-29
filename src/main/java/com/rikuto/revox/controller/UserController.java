package com.rikuto.revox.controller;

import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.dto.user.UserUpdateRequest;
import com.rikuto.revox.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ユーザー情報に関するコントローラーです。
 */
@Tag(name = "ユーザー情報に関する管理", description = "ユーザー情報の更新と論理削除を管理するエンドポイント群です。")
@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	// UPDATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 既存のユーザー情報を、受け取ったリクエスト内容に更新します。
	 */
	@Operation(summary = "ユーザー情報を更新する", description = "既存のユーザー情報を部分的に更新します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ユーザー情報の更新に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（バリデーションエラーなど）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない"),
			@ApiResponse(responseCode = "404", description = "ユーザーが見つからない")
	})
	@PatchMapping("/{userId}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<UserResponse> updateUserNickname(
			@RequestBody @Valid UserUpdateRequest request,
			@Parameter(description = "更新したいユーザーの一意の識別子。", required = true)
			@PathVariable @Positive Integer userId
	) {
		UserResponse response = userService.updateUser(request, userId);
		return ResponseEntity.ok(response);
	}

	/**
	 * ユーザーを論理削除します。
	 */
	@Operation(summary = "ユーザーを論理削除する", description = "指定されたユーザー情報を物理的に削除するのではなく、論理的に削除（非表示）にします。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "ユーザーの論理削除に成功"),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（ユーザーIDが不正など）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない"),
			@ApiResponse(responseCode = "404", description = "ユーザーが見つからない")
	})
	@PatchMapping("/{userId}/softDelete")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Void> softDeleteUser(
			@Parameter(description = "論理削除したいユーザーの一意の識別子。", required = true)
			@PathVariable @Positive Integer userId
	) {
		userService.softDeleteUser(userId);
		return ResponseEntity.noContent().build();
	}
}