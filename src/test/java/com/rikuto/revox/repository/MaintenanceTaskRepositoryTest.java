package com.rikuto.revox.repository;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.maintenancetask.MaintenanceTask;
import com.rikuto.revox.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("MaintenanceTaskRepositoryのテスト")
class MaintenanceTaskRepositoryTest {

	@Autowired
	private MaintenanceTaskRepository maintenanceTaskRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private BikeRepository bikeRepository;

	@Autowired
	private UserRepository userRepository;

	private User testUser;
	private Bike testBike;
	private Category testCategory;
	private Category otherCategory;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		bikeRepository.deleteAll();
		categoryRepository.deleteAll();
		maintenanceTaskRepository.deleteAll();

		testUser = userRepository.save(User.builder().uniqueUserId("test-user-id").nickname("testUser").build());

		testBike = bikeRepository.save(Bike.builder().user(testUser).manufacturer("TEST").modelName("testBike").build());

		testCategory = categoryRepository.save(Category.builder().name("testCategory").displayOrder(1).build());

		otherCategory = categoryRepository.save(Category.builder().name("otherCategory").displayOrder(2).build());
	}

	private MaintenanceTask createMaintenanceTask(Category category, Bike bike, String name, boolean isDeleted) {
		return maintenanceTaskRepository.save(MaintenanceTask.builder()
				.category(category)
				.bike(bike)

				.name(name)
				.description("description")

				.isDeleted(isDeleted)
				.build());
	}

	@Nested
	class FindByUserIdTests {
		@Test
		void 指定されたユーザーIDの整備タスクを最新順に5件取得できること() throws InterruptedException {
			createMaintenanceTask(testCategory, testBike, "Task 1", false);
			createMaintenanceTask(testCategory, testBike, "Task 2", false);
			createMaintenanceTask(testCategory, testBike, "Task 3", false);
			createMaintenanceTask(testCategory, testBike, "Task 4", false);
			createMaintenanceTask(testCategory, testBike, "Task 5", false);
			createMaintenanceTask(testCategory, testBike, "Task 6", false);

			User otherUser = userRepository.save(User.builder().uniqueUserId("other-user-id").nickname("otherUser").build());
			Bike otherBike = bikeRepository.save(Bike.builder().user(otherUser).manufacturer("otherTEST").modelName("otherBike").build());
			createMaintenanceTask(testCategory, otherBike, "otherUserTask", false);

			Pageable pageable = PageRequest.of(
					0, 5, Sort.by("createdAt").descending().and(Sort.by("id").descending()));
			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(testUser.getId(), pageable);

			assertThat(tasks).hasSize(5);
			assertThat(tasks).extracting(MaintenanceTask::getName).containsExactly("Task 6", "Task 5", "Task 4", "Task 3", "Task 2");
		}

		@Test
		void 存在しないユーザーIDを指定した場合に空のリストを返すこと() {
			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(999999999, Pageable.unpaged());

			assertThat(tasks).isEmpty();
		}

		@Test
		void ユーザーに紐づくタスクが存在しない場合に空のリストを返すこと() {
			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(testUser.getId(), Pageable.unpaged());

			assertThat(tasks).isEmpty();
		}

		@Test
		void 論理削除されたタスクは検索結果に含まれないこと() {
			createMaintenanceTask(testCategory, testBike, "Deleted Task", true);

			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(testUser.getId(), Pageable.unpaged());

			assertThat(tasks).isEmpty();
		}
	}

	@Nested
	class FindByBikeIdTests {
		@Test
		void 指定されたバイクIDの整備タスクを全件取得できること() {
			createMaintenanceTask(testCategory, testBike, "Bike Task 1", false);
			createMaintenanceTask(otherCategory, testBike, "Bike Task 2", false);

			Bike anotherBike = bikeRepository.save(Bike.builder().user(testUser).manufacturer("anotherTEST").modelName("anotherBike").build());
			createMaintenanceTask(testCategory, anotherBike, "anotherBikeTask", false);

			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(testBike.getId());

			assertThat(tasks).hasSize(2);
			assertThat(tasks).extracting(MaintenanceTask::getName).containsExactlyInAnyOrder("Bike Task 1", "Bike Task 2");
		}

		@Test
		void 存在しないバイクIDを指定した場合に空のリストを返すこと() {
			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(999999999);

			assertThat(tasks).isEmpty();
		}

		@Test
		void 指定されたバイクに紐づくタスクが存在しない場合に空のリストを返すこと() {
			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(testBike.getId());

			assertThat(tasks).isEmpty();
		}

		@Test
		void 論理削除されたタスクは検索結果に含まれないこと() {
			createMaintenanceTask(testCategory, testBike, "Deleted Task", true);

			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(testBike.getId());

			assertThat(tasks).isEmpty();
		}
	}

	@Nested
	class FindByBikeIdAndCategoryIdTests {
		@Test
		void 指定されたバイクIDでカテゴリーIDに紐づく整備タスクを全件取得できること() {
			createMaintenanceTask(testCategory, testBike, "Matching Task 1", false);
			createMaintenanceTask(testCategory, testBike, "Matching Task 2", false);

			createMaintenanceTask(otherCategory, testBike, "Other Category Task", false);

			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(testBike.getId(), testCategory.getId());

			assertThat(tasks).hasSize(2);
			assertThat(tasks).extracting(MaintenanceTask::getName).containsExactlyInAnyOrder("Matching Task 1", "Matching Task 2");
		}

		@Test
		void 指定されたバイクIDで存在しないカテゴリーIDを指定した場合に空のリストを返すこと() {
			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(testBike.getId(),999999999);

			assertThat(tasks).isEmpty();
		}

		@Test
		void 指定されたバイクIDでカテゴリーIDに紐づくタスクが存在しない場合に空のリストを返すこと() {
			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(testBike.getId(), testCategory.getId());

			assertThat(tasks).isEmpty();
		}

		@Test
		void 論理削除されたタスクは検索結果に含まれないこと() {
			createMaintenanceTask(testCategory, testBike, "Deleted Task", true);

			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(testBike.getId(), testCategory.getId());

			assertThat(tasks).isEmpty();
		}
	}

	@Nested
	class FindByCategoryIdAndTaskIdTests {
		@Test
		void 指定されたカテゴリーIDで整備タスクIDに紐づく単一の整備タスク情報を正しく取得できること() {
			MaintenanceTask maintenanceTask = createMaintenanceTask(testCategory, testBike, "testタスク", false);
			Integer maintenanceTaskId = maintenanceTask.getId();

			createMaintenanceTask(testCategory, testBike, "test", false);

			Optional<MaintenanceTask> task =
					maintenanceTaskRepository.findByCategoryIdAndIdAndIsDeletedFalse(testCategory.getId(), maintenanceTaskId);

			assertThat(task).isPresent();
			assertThat(task.get().getName()).isEqualTo("testタスク");
		}

		@Test
		void 指定されたカテゴリーIDで存在しない整備タスクIDに対して空のOptionalを返すこと() {
			Optional<MaintenanceTask> task = maintenanceTaskRepository.findByCategoryIdAndIdAndIsDeletedFalse(testCategory.getId(), 999999999);

			assertThat(task).isEmpty();
		}

		@Test
		void 論理削除されたタスクは検索結果に含まれないこと() {
			MaintenanceTask deletedTask = createMaintenanceTask(testCategory, testBike, "Deleted Task", true);

			Optional<MaintenanceTask> task = maintenanceTaskRepository.findByCategoryIdAndIdAndIsDeletedFalse(testCategory.getId(), deletedTask.getId());

			assertThat(task).isEmpty();
		}
	}
}