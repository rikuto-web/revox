package com.rikuto.revox.mapper;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.dto.category.CategoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

	public CategoryResponse toResponse(Category category) {

		return CategoryResponse.builder()
				.id(category.getId())

				.name(category.getName())
				.displayOrder(category.getDisplayOrder())

				.createdAt(category.getCreatedAt())
				.updatedAt(category.getUpdatedAt())
				.build();
	}

	public List<CategoryResponse> toResponseList(List<Category> categoryList) {

		return categoryList.stream()
				.map(this::toResponse)
				.toList();
	}
}
