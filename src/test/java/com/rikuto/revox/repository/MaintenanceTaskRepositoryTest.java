package com.rikuto.revox.repository;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.maintenancetask.MaintenanceTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MaintenanceTaskRepositoryTest {

	@Autowired
	private MaintenanceTaskRepository maintenanceTaskRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	private Category creatCategory(String name, Integer displayOrder) {
		return categoryRepository.save(Category.builder()
				.name(name)
				.displayOrder(displayOrder)
				.build());
	}

	private MaintenanceTask createMaintenanceTask(Category category,
	                                              String name,
	                                              String description,
	                                              boolean isDeleted){
		return maintenanceTaskRepository.save(MaintenanceTask.builder()
				.category(category)
				.name(name)
				.description(description)
				.isDeleted(isDeleted)
				.build());
	}

	@Test
	void 指定されたカテゴリーIDに紐づく整備タスク情報を全件正しく取得できること() {
		Category category = creatCategory("testCategory", 999999);
		Integer testCategoryId = category.getId();

		createMaintenanceTask(category, "testタスク", "テストテスト", false);
		createMaintenanceTask(category, "test", "テスト",false);

		Category otherCategory = creatCategory("testDummyCategory", 888888);
		createMaintenanceTask(otherCategory, "ダミーテスト", "ダミー",false);

		List<MaintenanceTask> maintenanceTaskList =
				maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(testCategoryId);

		assertThat(maintenanceTaskList).isNotNull();
		assertThat(maintenanceTaskList).hasSize(2);
		assertThat(maintenanceTaskList)
				.extracting(MaintenanceTask::getName)
				.containsExactlyInAnyOrder("testタスク", "test");
	}

	@Test
	void 指定されたカテゴリーIDに紐づく単一の整備タスク情報を正しく取得できること() {
		Category category = creatCategory("testCategory", 999999);
		Integer testCategoryId = category.getId();

		MaintenanceTask maintenanceTask =
				createMaintenanceTask(category, "testタスク", "テストテスト", false);
		Integer maintenanceTaskId = maintenanceTask.getId();

		createMaintenanceTask(category, "test", "テスト", false);

		Optional<MaintenanceTask> task =
				maintenanceTaskRepository.findByCategoryIdAndIdAndIsDeletedFalse(testCategoryId, maintenanceTaskId);

		assertThat(task).isPresent();
		assertThat(task.get().getName()).isEqualTo("testタスク");

	}

	@Test
	void 存在しないカテゴリーIDに対して空のリストを返すこと() {
		List<MaintenanceTask> tasksForNonExistentCategory =
				maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(999999999);

		assertThat(tasksForNonExistentCategory).isEmpty();
	}

	@Test
	void 整備タスク未登録のカテゴリーは空のリストを返すこと() {
		Category existingCategoryWithoutTasks = creatCategory("tテストカテゴリー", 999999999);

		List<MaintenanceTask> foundTaskList =
				maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(existingCategoryWithoutTasks.getId());

		assertThat(foundTaskList).isEmpty();
	}

	@Test
	void 論理削除された整備タスクは検索結果に含まれないこと() {
		Category testCategory = creatCategory("TestCategoryForDeletedTask", 100);
		createMaintenanceTask(testCategory, "Active Task", "This is an active task.", false);
		createMaintenanceTask(testCategory, "Deleted Task", "This task should be deleted.", true);

		List<MaintenanceTask> foundTaskList =
				maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(testCategory.getId());

		assertThat(foundTaskList).hasSize(1);
		assertThat(foundTaskList).extracting(MaintenanceTask::getName).containsExactly("Active Task");
		assertThat(foundTaskList).extracting(MaintenanceTask::getName).doesNotContain("Deleted Task");
	}
}