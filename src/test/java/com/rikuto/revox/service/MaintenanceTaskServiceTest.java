package com.rikuto.revox.service;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.maintenancetask.MaintenanceTask;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.MaintenanceTaskMapper;
import com.rikuto.revox.repository.CategoryRepository;
import com.rikuto.revox.repository.MaintenanceTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaintenanceTaskServiceTest {

	@Mock
	private MaintenanceTaskRepository maintenanceTaskRepository;

	@Mock
	private MaintenanceTaskMapper maintenanceTaskMapper;

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private MaintenanceTaskService maintenanceTaskService;

	private Category testCategory;
	private MaintenanceTask testMaintenanceTask;
	private MaintenanceTaskRequest commonMaintenanceTaskRequest;
	private MaintenanceTaskResponse commonMaintenanceTaskResponse;

	@BeforeEach
	void setUp() {
		testCategory = Category.builder().id(1).name("エンジン").displayOrder(1).build();
		testMaintenanceTask = MaintenanceTask.builder()
				.id(301)
				.category(testCategory)
				.name("オイル交換手順")
				.description("1. エンジンを温める\n2. ドレンボルトを外す\n3. 新しいオイルを注入する")
				.build();

		commonMaintenanceTaskRequest = MaintenanceTaskRequest.builder()
				.categoryId(testCategory.getId())
				.name("オイル交換手順")
				.description("1. エンジンを温める\n2. ドレンボルトを外す\n3. 新しいオイルを注入する")
				.build();

		commonMaintenanceTaskResponse = MaintenanceTaskResponse.builder()
				.id(testMaintenanceTask.getId())
				.categoryId(testCategory.getId())
				.name("オイル交換手順")
				.description("1. エンジンを温める\n2. ドレンボルトを外す\n3. 新しいオイルを注入する")
				.build();
	}

	private void stubCategoryFound() {
		when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
	}

	private void stubCategoryNotFound() {
		when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.empty());
	}

	private void stubMaintenanceTaskFound() {
		when(maintenanceTaskRepository.findById(testMaintenanceTask.getId())).thenReturn(Optional.of(testMaintenanceTask));
	}

	private void stubMaintenanceTaskNotFound() {
		when(maintenanceTaskRepository.findById(testMaintenanceTask.getId())).thenReturn(Optional.empty());
	}

	@Nested
	class FindMaintenanceTaskByCategoryIdTests {

		@Test
		void カテゴリーIDに紐づく整備タスクを正しく取得できること() {
			List<MaintenanceTask> maintenanceTasks = List.of(testMaintenanceTask);
			when(maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(testCategory.getId()))
					.thenReturn(maintenanceTasks);
			when(maintenanceTaskMapper.toResponseList(maintenanceTasks)).thenReturn(List.of(commonMaintenanceTaskResponse));

			List<MaintenanceTaskResponse> result = maintenanceTaskService.findMaintenanceTaskByCategoryId(testCategory.getId());

			assertThat(result).hasSize(1);
			assertThat(result.getFirst()).isEqualTo(commonMaintenanceTaskResponse);
			verify(maintenanceTaskRepository).findByCategoryIdAndIsDeletedFalse(testCategory.getId());
			verify(maintenanceTaskMapper).toResponseList(maintenanceTasks);
		}

		@Test
		void カテゴリーに整備タスクがない場合は空のリストを返すこと() {
			when(maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(testCategory.getId()))
					.thenReturn(List.of());
			when(maintenanceTaskMapper.toResponseList(List.of())).thenReturn(List.of());

			List<MaintenanceTaskResponse> result = maintenanceTaskService.findMaintenanceTaskByCategoryId(testCategory.getId());

			assertThat(result).isEmpty();
			verify(maintenanceTaskRepository).findByCategoryIdAndIsDeletedFalse(testCategory.getId());
			verify(maintenanceTaskMapper).toResponseList(List.of());
		}
	}

	@Nested
	class RegisterMaintenanceTaskTests {

		@Test
		void 新しい整備タスクが正常に登録され登録された整備タスク情報が返されること() {
			stubCategoryFound();
			when(maintenanceTaskRepository.save(testMaintenanceTask)).thenReturn(testMaintenanceTask);
			when(maintenanceTaskMapper.toEntity(commonMaintenanceTaskRequest, testCategory)).thenReturn(testMaintenanceTask);
			when(maintenanceTaskMapper.toResponse(testMaintenanceTask)).thenReturn(commonMaintenanceTaskResponse);

			MaintenanceTaskResponse result = maintenanceTaskService.registerMaintenanceTask(commonMaintenanceTaskRequest);

			assertThat(result).isEqualTo(commonMaintenanceTaskResponse);
			verify(categoryRepository).findById(testCategory.getId());
			verify(maintenanceTaskRepository).save(testMaintenanceTask);
			verify(maintenanceTaskMapper).toEntity(commonMaintenanceTaskRequest, testCategory);
			verify(maintenanceTaskMapper).toResponse(testMaintenanceTask);
		}

		@Test
		void カテゴリーが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubCategoryNotFound();

			assertThatThrownBy(() -> maintenanceTaskService.registerMaintenanceTask(commonMaintenanceTaskRequest))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("カテゴリーID " + testCategory.getId() + " が見つかりません。");

			verify(maintenanceTaskRepository, never()).save(any());
			verify(maintenanceTaskMapper, never()).toEntity(any(), any());
		}
	}

	@Nested
	class UpdateMaintenanceTaskTests {

		@Test
		void 既存の整備タスクが正常に更新され更新された整備タスク情報が返されること() {
			stubMaintenanceTaskFound();
			MaintenanceTaskUpdateRequest updateRequest = MaintenanceTaskUpdateRequest.builder()
					.name("更新されたタスク名")
					.description("更新された説明")
					.build();
			when(maintenanceTaskRepository.save(testMaintenanceTask)).thenReturn(testMaintenanceTask);

			MaintenanceTaskResponse updatedResponse = MaintenanceTaskResponse.builder()
					.id(testMaintenanceTask.getId())
					.name("更新されたタスク名")
					.build();
			when(maintenanceTaskMapper.toResponse(testMaintenanceTask)).thenReturn(updatedResponse);

			MaintenanceTaskResponse result = maintenanceTaskService.updateMaintenanceTask(testMaintenanceTask.getId(), updateRequest);

			assertThat(result).isEqualTo(updatedResponse);
			verify(maintenanceTaskRepository).findById(testMaintenanceTask.getId());
			verify(maintenanceTaskRepository).save(testMaintenanceTask);
			verify(maintenanceTaskMapper).toResponse(testMaintenanceTask);
		}

		@Test
		void 整備タスクが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubMaintenanceTaskNotFound();
			MaintenanceTaskUpdateRequest updateRequest = MaintenanceTaskUpdateRequest.builder()
					.name("更新されたタスク名")
					.description("更新された説明")
					.build();

			assertThatThrownBy(() -> maintenanceTaskService.updateMaintenanceTask(testMaintenanceTask.getId(), updateRequest))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("整備タスクID " + testMaintenanceTask.getId() + " が見つかりません。");

			verify(maintenanceTaskRepository, never()).save(any());
		}
	}

	@Nested
	class SoftDeleteMaintenanceTaskTests {

		@Test
		void 登録されている整備タスクが正常に論理削除されること() {
			stubMaintenanceTaskFound();
			when(maintenanceTaskRepository.save(testMaintenanceTask)).thenReturn(testMaintenanceTask);

			maintenanceTaskService.softDeleteMaintenanceTask(testMaintenanceTask.getId());

			assertThat(testMaintenanceTask.isDeleted()).isTrue();
			verify(maintenanceTaskRepository).findById(testMaintenanceTask.getId());
			verify(maintenanceTaskRepository).save(testMaintenanceTask);
		}

		@Test
		void 整備タスクが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubMaintenanceTaskNotFound();

			assertThatThrownBy(() -> maintenanceTaskService.softDeleteMaintenanceTask(testMaintenanceTask.getId()))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("整備タスクID " + testMaintenanceTask.getId() + " が見つかりません。");

			verify(maintenanceTaskRepository, never()).save(any());
		}
	}
}