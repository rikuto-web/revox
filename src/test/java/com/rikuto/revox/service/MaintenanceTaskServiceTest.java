package com.rikuto.revox.service;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.maintenancetask.MaintenanceTask;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskRequest;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskResponse;
import com.rikuto.revox.dto.maintenancetask.MaintenanceTaskUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.MaintenanceTaskMapper;
import com.rikuto.revox.repository.BikeRepository;
import com.rikuto.revox.repository.CategoryRepository;
import com.rikuto.revox.repository.MaintenanceTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
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

	@Mock
	private BikeRepository bikeRepository;

	@InjectMocks
	private MaintenanceTaskService maintenanceTaskService;

	private Category testCategory;

	private Bike testBike;

	private MaintenanceTask testMaintenanceTask;

	private MaintenanceTaskRequest commonMaintenanceTaskRequest;

	private MaintenanceTaskResponse commonMaintenanceTaskResponse;

	@BeforeEach
	void setUp() {
		testCategory = Category.builder().id(1).name("エンジン").displayOrder(1).build();

		testBike = Bike.builder().id(101).modelName("CBR250RR").build();

		testMaintenanceTask = MaintenanceTask.builder()
				.id(301)
				.bike(testBike)
				.category(testCategory)
				.name("オイル交換手順")
				.description("1. エンジンを温める\n2. ドレンボルトを外す\n3. 新しいオイルを注入する")
				.build();

		commonMaintenanceTaskRequest = MaintenanceTaskRequest.builder()
				.categoryId(testCategory.getId())
				.bikeId(testBike.getId())
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

	private void stubBikeFound() {
		when(bikeRepository.findById(testBike.getId())).thenReturn(Optional.of(testBike));
	}

	private void stubMaintenanceTaskFound() {
		when(maintenanceTaskRepository.findById(testMaintenanceTask.getId())).thenReturn(Optional.of(testMaintenanceTask));
	}

	private void stubMaintenanceTaskNotFound() {
		when(maintenanceTaskRepository.findById(testMaintenanceTask.getId())).thenReturn(Optional.empty());
	}

	@Nested
	class RegisterMaintenanceTaskTests {
		@Test
		void 新しい整備タスクが正常に登録され登録された整備タスク情報が返されること() {
			stubCategoryFound();
			stubBikeFound();
			when(maintenanceTaskMapper.toEntity(commonMaintenanceTaskRequest, testBike, testCategory)).thenReturn(testMaintenanceTask);
			when(maintenanceTaskRepository.save(testMaintenanceTask)).thenReturn(testMaintenanceTask);
			when(maintenanceTaskMapper.toResponse(testMaintenanceTask)).thenReturn(commonMaintenanceTaskResponse);

			MaintenanceTaskResponse result = maintenanceTaskService.registerMaintenanceTask(commonMaintenanceTaskRequest);

			assertThat(result).isEqualTo(commonMaintenanceTaskResponse);
			verify(categoryRepository).findById(testCategory.getId());
			verify(bikeRepository).findById(testBike.getId());
			verify(maintenanceTaskRepository).save(testMaintenanceTask);
			verify(maintenanceTaskMapper).toEntity(commonMaintenanceTaskRequest, testBike, testCategory);
			verify(maintenanceTaskMapper).toResponse(testMaintenanceTask);
		}

		@Test
		void カテゴリーが見つからない場合にResourceNotFoundExceptionをスローすること() {
			when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.empty());

			assertThatThrownBy(() -> maintenanceTaskService.registerMaintenanceTask(commonMaintenanceTaskRequest))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("カテゴリーID " + testCategory.getId() + " が見つかりません。");

			verify(bikeRepository, never()).findById(any());
			verify(maintenanceTaskRepository, never()).save(any());
		}

		@Test
		void バイクが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubCategoryFound();
			when(bikeRepository.findById(testBike.getId())).thenReturn(Optional.empty());

			assertThatThrownBy(() -> maintenanceTaskService.registerMaintenanceTask(commonMaintenanceTaskRequest))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("バイクID " + testBike.getId() + " が見つかりません。");

			verify(categoryRepository).findById(testCategory.getId());
			verify(maintenanceTaskRepository, never()).save(any());
		}
	}

	@Nested
	class FindLatestMaintenanceTasksByUserIdTests {
		@Test
		void ユーザーIDに紐づく最新の整備タスクを指定件数取得できること() {
			Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
			List<MaintenanceTask> tasks = List.of(testMaintenanceTask);
			when(maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(any(), any(Pageable.class))).thenReturn(tasks);
			when(maintenanceTaskMapper.toResponseList(tasks)).thenReturn(List.of(commonMaintenanceTaskResponse));

			List<MaintenanceTaskResponse> result = maintenanceTaskService.findLatestMaintenanceTasksByUserId(1);

			assertThat(result).hasSize(1);
			assertThat(result.getFirst()).isEqualTo(commonMaintenanceTaskResponse);
			verify(maintenanceTaskRepository).findByBike_UserIdAndIsDeletedFalse(any(), any(Pageable.class));
			verify(maintenanceTaskMapper).toResponseList(tasks);
		}

		@Test
		void ユーザーに紐づくタスクが存在しない場合に空のリストを返すこと() {
			when(maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(any(), any(Pageable.class))).thenReturn(Collections.emptyList());
			when(maintenanceTaskMapper.toResponseList(any())).thenReturn(Collections.emptyList());

			List<MaintenanceTaskResponse> result = maintenanceTaskService.findLatestMaintenanceTasksByUserId(999);

			assertThat(result).isEmpty();
			verify(maintenanceTaskRepository).findByBike_UserIdAndIsDeletedFalse(any(), any(Pageable.class));
			verify(maintenanceTaskMapper).toResponseList(any());
		}
	}

	@Nested
	class FindByBikeIdTests {
		@Test
		void バイクIDに紐づく整備タスクをすべて取得できること() {
			List<MaintenanceTask> tasks = List.of(testMaintenanceTask);
			when(maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(testBike.getId())).thenReturn(tasks);
			when(maintenanceTaskMapper.toResponseList(tasks)).thenReturn(List.of(commonMaintenanceTaskResponse));

			List<MaintenanceTaskResponse> result = maintenanceTaskService.findByBikeId(testBike.getId());

			assertThat(result).hasSize(1);
			assertThat(result.getFirst()).isEqualTo(commonMaintenanceTaskResponse);
			verify(maintenanceTaskRepository).findByBikeIdAndIsDeletedFalse(testBike.getId());
			verify(maintenanceTaskMapper).toResponseList(tasks);
		}

		@Test
		void バイクに紐づくタスクがない場合に空のリストを返すこと() {
			when(maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(testBike.getId())).thenReturn(Collections.emptyList());
			when(maintenanceTaskMapper.toResponseList(any())).thenReturn(Collections.emptyList());

			List<MaintenanceTaskResponse> result = maintenanceTaskService.findByBikeId(testBike.getId());

			assertThat(result).isEmpty();
			verify(maintenanceTaskRepository).findByBikeIdAndIsDeletedFalse(testBike.getId());
			verify(maintenanceTaskMapper).toResponseList(any());
		}
	}

	@Nested
	class FindByBikeIdAndCategoryIdTests {
		@Test
		void バイクIDとカテゴリーIDに紐づくタスクをすべて取得できること() {
			List<MaintenanceTask> tasks = List.of(testMaintenanceTask);
			when(maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(testBike.getId(), testCategory.getId())).thenReturn(tasks);
			when(maintenanceTaskMapper.toResponseList(tasks)).thenReturn(List.of(commonMaintenanceTaskResponse));

			List<MaintenanceTaskResponse> result = maintenanceTaskService.findByBikeIdAndCategoryId(testBike.getId(), testCategory.getId());

			assertThat(result).hasSize(1);
			assertThat(result.getFirst()).isEqualTo(commonMaintenanceTaskResponse);
			verify(maintenanceTaskRepository).findByBikeIdAndCategoryIdAndIsDeletedFalse(testBike.getId(), testCategory.getId());
			verify(maintenanceTaskMapper).toResponseList(tasks);
		}

		@Test
		void 条件に紐づくタスクがない場合に空のリストを返すこと() {
			when(maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(any(), any())).thenReturn(Collections.emptyList());
			when(maintenanceTaskMapper.toResponseList(any())).thenReturn(Collections.emptyList());

			List<MaintenanceTaskResponse> result = maintenanceTaskService.findByBikeIdAndCategoryId(testBike.getId(), testCategory.getId());

			assertThat(result).isEmpty();
			verify(maintenanceTaskRepository).findByBikeIdAndCategoryIdAndIsDeletedFalse(any(), any());
			verify(maintenanceTaskMapper).toResponseList(any());
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