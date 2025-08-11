package com.rikuto.revox.controller;

import com.rikuto.revox.dto.user.UserResponse;
import com.rikuto.revox.dto.user.UserUpdateRequest;
import com.rikuto.revox.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ユーザー情報のCRUD操作を扱うコントローラーです。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 既存のユーザー情報を、受け取ったリクエスト内容に更新します。
	 * PUT　/api/users/{userId}
	 *
	 * @param request 更新されたユーザー情報を含むリクエストDTO
	 * @param userId ユーザーID
	 * @return 更新されたユーザー情報とHTTPステータス200 OK
	 */
	@PutMapping("/{userId}")
	public ResponseEntity<UserResponse> updateUserNickname (@RequestBody @Valid UserUpdateRequest request,
	                                                        @PathVariable @Positive Integer userId){
		UserResponse response = userService.updateUser(request, userId);

		return ResponseEntity.ok(response);
	}

	/**
	 * ユーザーを論理削除します。
	 * PATCH　/api/users/{userId}/delete
	 *
	 * @param userId ユーザーID
	 * @return HTTPステータス204 No Content
	 */
	@PatchMapping("/{userId}/delete")
	public ResponseEntity<Void> softDeleteUser (@PathVariable @Positive Integer userId){
		userService.softDeleteUser(userId);

		return ResponseEntity.noContent().build();
	}
}
