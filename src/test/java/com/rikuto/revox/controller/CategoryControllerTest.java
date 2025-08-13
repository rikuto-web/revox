package com.rikuto.revox.controller;

import com.rikuto.revox.dto.category.CategoryResponse;
import com.rikuto.revox.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
		controllers = CategoryController.class,
		excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@Import(CategoryControllerTest.CategoryServiceTestConfig.class)
class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CategoryService categoryService;

	private CategoryResponse commonCategoryResponse;

	private final Integer testCategoryId = 1;

	@BeforeEach
	void setUp() {
		commonCategoryResponse = CategoryResponse.builder()
				.id(testCategoryId)
				.name("テストカテゴリ")
				.build();

		reset(categoryService);
	}

	@Test
	void 全カテゴリー情報を正常に取得し200OKを返すこと() throws Exception {
		List<CategoryResponse> expectedList = List.of(commonCategoryResponse);
		when(categoryService.findAllCategories()).thenReturn(expectedList);

		mockMvc.perform(get("/api/categories")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(testCategoryId))
				.andExpect(jsonPath("$[0].name").value("テストカテゴリ"));

		verify(categoryService, times(1)).findAllCategories();
	}

	@Test
	@DisplayName("サービスで予期せぬ例外が発生した場合、500を返すこと")
	void getAllCategories_unexpectedException() throws Exception {
		when(categoryService.findAllCategories()).thenThrow(new RuntimeException("データベース接続エラー"));

		mockMvc.perform(get("/api/categories")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

		verify(categoryService, times(1)).findAllCategories();
	}

	/**
	 * テスト用の設定クラス
	 * CategoryServiceのモックBeanを定義します。
	 */
	@TestConfiguration
	static class CategoryServiceTestConfig {
		@Bean
		public CategoryService categoryService() {
			return Mockito.mock(CategoryService.class);
		}
	}
}