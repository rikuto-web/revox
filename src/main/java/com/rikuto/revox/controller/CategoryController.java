package com.rikuto.revox.controller;

import com.rikuto.revox.dto.category.CategoryResponse;
import com.rikuto.revox.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * カテゴリー情報に関するコントローラーです。
 */
@Tag(name = "カテゴリーに関する管理", description = "バイクのカテゴリー情報を管理するエンドポイント群です。")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * 全てのカテゴリー情報を取得します。
	 */
	@Operation(summary = "全カテゴリー情報を取得する", description = "全てのバイクカテゴリー情報をリスト形式で取得します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "カテゴリー情報の取得に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = CategoryResponse.class))),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない")
	})
	@GetMapping
	@PreAuthorize("hasAnyRole('GUEST', 'USER')")
	public ResponseEntity<List<CategoryResponse>> getAllCategories() {
		List<CategoryResponse> categorieList = categoryService.findAllCategories();

		return ResponseEntity.ok(categorieList);
	}
}