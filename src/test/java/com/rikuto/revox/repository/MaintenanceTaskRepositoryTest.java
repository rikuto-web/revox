package com.rikuto.revox.repository;

import com.rikuto.revox.entity.Category;
import com.rikuto.revox.entity.MaintenanceTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MaintenanceTaskRepositoryTest {

	@Autowired
	private MaintenanceTaskRepository maintenanceTaskRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();
	}

	@Test
	void 指定されたカテゴリーIDに紐づく整備タスク情報を正しく取得できること() {
		// Given: テストカテゴリーと整備タスクの準備
		Category testCategory = Category.builder()
				.name("testCategory")
				.displayOrder(999999999)
				.createdAt(now)
				.updatedAt(now)
				.build();
		// テストカテゴリーを保存し、生成されたIDを取得
		Category savedTestCategory = categoryRepository.save(testCategory);
		Integer testCategoryId = savedTestCategory.getId();

		//テストカテゴリーは２つの整備タスクを保有
		MaintenanceTask testFirstTask = MaintenanceTask.builder()
				.category(savedTestCategory)
				.name("testタスク")
				.description("テストテスト")
				.createdAt(now)
				.updatedAt(now)
				.build();

		MaintenanceTask testSecondTask = MaintenanceTask.builder()
				.category(savedTestCategory)
				.name("テストタスク")
				.description("testTest")
				.createdAt(now)
				.updatedAt(now)
				.build();

		// 意図しないデータが取得されないことを確認するため、別カテゴリーの整備タスクも作成
		Category anotherCategory = Category.builder()
				.name("categoryTest")
				.displayOrder(888888888)
				.createdAt(now)
				.updatedAt(now)
				.build();
		Category savedAnotherCategory = categoryRepository.save(anotherCategory);

		MaintenanceTask anotherCategoryByTask = MaintenanceTask.builder()
				.category(savedAnotherCategory)
				.name("テストタスク")
				.description("testTest")
				.createdAt(now)
				.updatedAt(now)
				.build();

		//３つの整備タスク情報を保存
		maintenanceTaskRepository.save(testFirstTask);
		maintenanceTaskRepository.save(testSecondTask);
		maintenanceTaskRepository.save(anotherCategoryByTask);

		// When: 指定されたカテゴリーIDに紐づく整備タスク情報を検索
		List<MaintenanceTask> maintenanceTasksForCategory = maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(testCategoryId);

		// Then: 正しいカテゴリー情報のみが取得されていることを確認
		assertThat(maintenanceTasksForCategory).isNotNull();
		assertThat(maintenanceTasksForCategory).hasSize(2);
		assertThat(maintenanceTasksForCategory)
				.extracting(MaintenanceTask::getName)
				.containsExactlyInAnyOrder("testタスク", "テストタスク");
	}

	@Test
	void 存在しないカテゴリーIDに対して空のリストを返すこと() {
		// When: 存在しないユーザーIDに紐づくバイク情報を検索
		List<MaintenanceTask> tasksForNonExistentCategory = maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(999999999);

		// Then: ユーザーが見つからず空のリストが返ってくる
		assertThat(tasksForNonExistentCategory).isEmpty();
	}

	@Test
	void 整備タスク未登録のカテゴリーは空のリストを返すこと() {
		// Given: 既存のカテゴリーと同等のデータをテスト内で準備
		Category category = Category.builder()
				.name("testCategory")
				.displayOrder(999999999)
				.createdAt(now)
				.updatedAt(now)
				.build();
		Category existingCategoryWithoutTasks = categoryRepository.save(category);

		// When: 存在するがタスク未登録のカテゴリーIDに紐づく整備タスク情報を検索
		List<MaintenanceTask> foundTasks = maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(existingCategoryWithoutTasks.getId());

		// Then: 整備タスクが見つからず空のリストが返ってくる
		assertThat(foundTasks).isEmpty();
	}

	@Test
	void 論理削除された整備タスクは検索結果に含まれないこと() {
		// Given: テストカテゴリーと、アクティブなタスク、論理削除されたタスクの準備
		Category testCategory = categoryRepository.save(Category.builder()
				.name("TestCategoryForDeletedTask")
				.displayOrder(100)
				.createdAt(now)
				.updatedAt(now)
				.build());

		MaintenanceTask activeTask = MaintenanceTask.builder()
				.category(testCategory)
				.name("Active Task")
				.description("This is an active task.")
				.createdAt(now)
				.updatedAt(now)
				.build();
		maintenanceTaskRepository.save(activeTask);

		MaintenanceTask softDeletedTask = MaintenanceTask.builder()
				.category(testCategory)
				.name("Deleted Task")
				.description("This task should be deleted.")
				.isDeleted(true)
				.createdAt(now.minusDays(1))
				.updatedAt(now.minusDays(1))
				.build();
		maintenanceTaskRepository.save(softDeletedTask);

		// When: カテゴリーIDで整備タスクを検索 (論理削除されていないもののみ)
		List<MaintenanceTask> foundTasks = maintenanceTaskRepository.findByCategoryIdAndIsDeletedFalse(testCategory.getId());

		// Then: アクティブなタスクのみが取得され、論理削除されたタスクは含まれないことを確認
		assertThat(foundTasks).hasSize(1);
		assertThat(foundTasks).extracting(MaintenanceTask::getName).containsExactly("Active Task");
		assertThat(foundTasks).extracting(MaintenanceTask::getName).doesNotContain("Deleted Task");
	}
}