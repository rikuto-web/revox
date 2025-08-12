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

	private Category category1;
	private Category category2;

	private CategoryResponse categoryResponse1;
	private CategoryResponse categoryResponse2;

	@BeforeEach
	void setUp() {
		category1 = Category.builder()
				.id(1)
				.name("エンジン")
				.displayOrder(1)
				.build();

		category2 = Category.builder()
				.id(2)
				.name("ブレーキ")
				.displayOrder(2)
				.build();

		categoryResponse1 = CategoryResponse.builder()
				.id(1)
				.name("エンジン")
				.displayOrder(1)
				.build();

		categoryResponse2 = CategoryResponse.builder()
				.id(2)
				.name("ブレーキ")
				.displayOrder(2)
				.build();
	}

	@Test
	void すべてのカテゴリーを複数件取得できること() {
		List<Category> categories = List.of(category1, category2);
		List<CategoryResponse> responses = List.of(categoryResponse1, categoryResponse2);

		when(categoryRepository.findAll()).thenReturn(categories);
		when(categoryMapper.toResponseList(categories)).thenReturn(responses);

		List<CategoryResponse> result = categoryService.findAllCategories();

		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(categoryResponse1, categoryResponse2);

		verify(categoryRepository).findAll();
		verify(categoryMapper).toResponseList(categories);
	}
}
