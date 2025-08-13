package com.rikuto.revox.service;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.dto.category.CategoryResponse;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.CategoryMapper;
import com.rikuto.revox.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
		this.categoryRepository = categoryRepository;
		this.categoryMapper = categoryMapper;
	}

	/**
	 * すべてのカテゴリーを取得します。
	 *
	 * @return すべてのカテゴリーリスト
	 */
	public List<CategoryResponse> findAllCategories() {
		List<Category> categories = categoryRepository.findAll();

		return categoryMapper.toResponseList(categories);
	}
}
