package com.rikuto.revox.service;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.dto.category.CategoryResponse;
import com.rikuto.revox.mapper.CategoryMapper;
import com.rikuto.revox.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional(readOnly = true)
	public List<CategoryResponse> findAllCategories() {
		List<Category> categorieList = categoryRepository.findAll();

		return categoryMapper.toResponseList(categorieList);
	}
}
