package com.rikuto.revox.service;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.dto.bike.BikeResponse;
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

	public CategoryResponse findCategoryById(Integer categoryId) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリーが見つかりません"));
		return categoryMapper.toResponse(category);
	}

	public List<CategoryResponse> findAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		return categoryMapper.toResponseList(categories);
	}

}
