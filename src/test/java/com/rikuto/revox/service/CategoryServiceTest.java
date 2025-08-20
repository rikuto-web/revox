package com.rikuto.revox.service;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.dto.category.CategoryResponse;
import com.rikuto.revox.mapper.CategoryMapper;
import com.rikuto.revox.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private CategoryMapper categoryMapper;

	@InjectMocks
	private CategoryService categoryService;

	private Category category;
	private Category secondCategory;

	private CategoryResponse categoryResponse;
	private CategoryResponse secondCategoryResponse;

	@BeforeEach
	void setUp() {
		category = Category.builder()
				.id(1)
				.name("TestCategory")
				.displayOrder(1)
				.build();

		secondCategory = Category.builder()
				.id(2)
				.name("テストカテゴリー")
				.displayOrder(2)
				.build();

		categoryResponse = CategoryResponse.builder()
				.id(1)
				.name("TestCategory")
				.displayOrder(1)
				.build();

		secondCategoryResponse = CategoryResponse.builder()
				.id(2)
				.name("テストカテゴリー")
				.displayOrder(2)
				.build();
	}

	@Test
	void すべてのカテゴリーを複数件取得できること() {
		List<Category> categories = List.of(category, secondCategory);
		List<CategoryResponse> responses = List.of(categoryResponse, secondCategoryResponse);

		when(categoryRepository.findAll()).thenReturn(categories);
		when(categoryMapper.toResponseList(categories)).thenReturn(responses);

		List<CategoryResponse> result = categoryService.findAllCategories();

		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(categoryResponse, secondCategoryResponse);

		verify(categoryRepository).findAll();
		verify(categoryMapper).toResponseList(categories);
	}
}
