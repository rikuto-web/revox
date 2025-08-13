package com.rikuto.revox.controller;

import com.rikuto.revox.dto.category.CategoryResponse;
import com.rikuto.revox.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * カテゴリー情報の取得を扱うコントローラーです。
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * 全てのカテゴリー情報を取得します。
	 * GET /api/categories
	 *
	 * @return 全てのカテゴリー情報とHTTPステータス200 OK
	 */
	@GetMapping
	public ResponseEntity<List<CategoryResponse>> getAllCategories() {
		List<CategoryResponse> categorieList = categoryService.findAllCategories();

		return ResponseEntity.ok(categorieList);
	}
}