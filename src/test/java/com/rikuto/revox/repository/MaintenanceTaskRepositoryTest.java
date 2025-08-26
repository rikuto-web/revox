package com.rikuto.revox.repository;

import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.maintenancetask.MaintenanceTask;
import com.rikuto.revox.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
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

	private void createMaintenanceTask(Category category, Bike bike, String name, boolean isDeleted) {
		maintenanceTaskRepository.save(MaintenanceTask.builder()
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
			//正常整備タスク
			createMaintenanceTask(testCategory, testBike, "Task1", false);
			createMaintenanceTask(testCategory, testBike, "Task2", false);
			createMaintenanceTask(testCategory, testBike, "Task3", false);
			createMaintenanceTask(testCategory, testBike, "Task4", false);
			createMaintenanceTask(testCategory, testBike, "Task5", false);
			createMaintenanceTask(testCategory, testBike, "Task6", false);

			//他ユーザーの整備タスク
			User otherUser = userRepository.save(User.builder().uniqueUserId("other-user-id").nickname("otherUser").build());
			Bike otherBike = bikeRepository.save(Bike.builder().user(otherUser).manufacturer("otherTEST").modelName("otherBike").build());
			createMaintenanceTask(testCategory, otherBike, "otherUserTask", false);

			Pageable pageable = PageRequest.of(
					0, 5, Sort.by("createdAt").descending().and(Sort.by("id").descending()));
			List<MaintenanceTask> result = maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(testUser.getId(), pageable);

			assertThat(result).hasSize(5);
			assertThat(result).extracting(MaintenanceTask::getName).containsExactly("Task6", "Task5", "Task4", "Task3", "Task2");
		}

		@Test
		void 存在しないユーザーIDを指定した場合に空のリストを返すこと() {
			List<MaintenanceTask> result = maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(999999999, Pageable.unpaged());

			assertThat(result).isEmpty();
		}

		@Test
		void ユーザーに紐づくタスクが存在しない場合に空のリストを返すこと() {
			List<MaintenanceTask> result = maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(testUser.getId(), Pageable.unpaged());

			assertThat(result).isEmpty();
		}

		@Test
		void 論理削除されたタスクは検索結果に含まれないこと() {
			createMaintenanceTask(testCategory, testBike, "DeletedTask", true);

			List<MaintenanceTask> result = maintenanceTaskRepository.findByBike_UserIdAndIsDeletedFalse(testUser.getId(), Pageable.unpaged());

			assertThat(result).isEmpty();
		}
	}

	@Nested
	class FindByBikeIdTests {
		@Test
		void 指定されたバイクIDの整備タスクを全件取得できること() {
			createMaintenanceTask(testCategory, testBike, "BikeTask", false);
			createMaintenanceTask(otherCategory, testBike, "BikeSecondTask", false);

			Bike anotherBike = bikeRepository.save(Bike.builder().user(testUser).manufacturer("anotherTEST").modelName("anotherBike").build());
			createMaintenanceTask(testCategory, anotherBike, "anotherBikeTask", false);

			List<MaintenanceTask> result = maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(testBike.getId());

			assertThat(result).hasSize(2);
			assertThat(result).extracting(MaintenanceTask::getName).containsExactlyInAnyOrder("BikeTask", "BikeSecondTask");
		}

		@Test
		void 存在しないバイクIDを指定した場合に空のリストを返すこと() {
			List<MaintenanceTask> result = maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(999999999);

			assertThat(result).isEmpty();
		}

		@Test
		void 指定されたバイクに紐づくタスクが存在しない場合に空のリストを返すこと() {
			List<MaintenanceTask> result = maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(testBike.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void 論理削除されたタスクは検索結果に含まれないこと() {
			createMaintenanceTask(testCategory, testBike, "DeletedTask", true);

			List<MaintenanceTask> result = maintenanceTaskRepository.findByBikeIdAndIsDeletedFalse(testBike.getId());

			assertThat(result).isEmpty();
		}
	}

	@Nested
	class FindByBikeIdAndCategoryIdTests {
		@Test
		void 指定されたバイクIDでカテゴリーIDに紐づく整備タスクを全件取得できること() {
			createMaintenanceTask(testCategory, testBike, "MatchingTask", false);
			createMaintenanceTask(testCategory, testBike, "MatchingSecondTask", false);
			createMaintenanceTask(otherCategory, testBike, "OtherCategoryTask", false);

			List<MaintenanceTask> result = maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(testBike.getId(), testCategory.getId());

			assertThat(result).hasSize(2);
			assertThat(result).extracting(MaintenanceTask::getName).containsExactlyInAnyOrder("MatchingTask", "MatchingSecondTask");
		}

		@Test
		void 指定されたバイクIDで存在しないカテゴリーIDを指定した場合に空のリストを返すこと() {
			List<MaintenanceTask> result = maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(testBike.getId(), 999999999);

			assertThat(result).isEmpty();
		}

		@Test
		void 指定されたバイクIDでカテゴリーIDに紐づくタスクが存在しない場合に空のリストを返すこと() {
			List<MaintenanceTask> result = maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(testBike.getId(), testCategory.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void 論理削除されたタスクは検索結果に含まれないこと() {
			createMaintenanceTask(testCategory, testBike, "DeletedTask", true);

			List<MaintenanceTask> tasks = maintenanceTaskRepository.findByBikeIdAndCategoryIdAndIsDeletedFalse(testBike.getId(), testCategory.getId());

			assertThat(tasks).isEmpty();
		}
	}
}