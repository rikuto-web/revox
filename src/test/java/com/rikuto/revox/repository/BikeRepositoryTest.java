package com.rikuto.revox.repository;

import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.domain.bike.Bike;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BikeRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BikeRepository bikeRepository;

	private User user;
	private User anotherUser;

	@BeforeEach
	void setUp() {
		user = createUser("TestUser");
		anotherUser = createUser("AnotherUser");
	}

	private User createUser(String nickname) {
		return userRepository.save(User.builder()
				.nickname(nickname)
				.uniqueUserId(java.util.UUID.randomUUID().toString())
				.build());
	}

	private Bike createBike(User user, String manufacturer, String modelName, String modelCode, int modelYear, boolean isDeleted) {
		return bikeRepository.save(Bike.builder()
				.user(user)
				.manufacturer(manufacturer)
				.modelName(modelName)
				.modelCode(modelCode)
				.modelYear(modelYear)
				.isDeleted(isDeleted)
				.build());
	}

	@Nested
	class FindByUserIdTests {
		@Test
		void ユーザーIDに紐づくバイク情報を正しく取得できること() {
			createBike(user, "TestMfr", "Z1", "TST-001", 2023, false);
			createBike(user, "TestTestMfr", "Z2", "TST-002", 2024, false);

			List<Bike> bikeList = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

			assertThat(bikeList).hasSize(2);
			assertThat(bikeList).extracting(Bike::getModelName).containsExactlyInAnyOrder("Z1", "Z2");
		}

		@Test
		void 論理削除されたバイクは検索結果に含まれないこと() {
			createBike(user, "TestMfr", "Z1", "TST-001", 2023, false);
			createBike(user, "TestTestMfr", "Z2", "TST-002", 2024, true);

			List<Bike> bikeList = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

			assertThat(bikeList).hasSize(1);
			assertThat(bikeList.getFirst().getModelName()).isEqualTo("Z1");
		}

		@Test
		void 別ユーザーのバイクは検索結果に含まれないこと() {
			Bike bike = createBike(user, "TestMfr", "Z1", "TST-001", 2023, false);
			createBike(anotherUser, "TestTestMfr", "Z2", "TST-002", 2024, false);

			List<Bike> bikeList = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

			assertThat(bikeList).hasSize(1);
			assertThat(bikeList.getFirst().getId()).isEqualTo(bike.getId());
		}

		@Test
		void 存在しないユーザーIDに対して空のリストを返すこと() {
			List<Bike> bikeList = bikeRepository.findByUserIdAndIsDeletedFalse(999999);

			assertThat(bikeList).isEmpty();
		}

		@Test
		void バイク未登録ユーザーには空のリストを返すこと() {
			List<Bike> bikeList = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

			assertThat(bikeList).isEmpty();
		}
	}

	@Nested
	class FindByIdAndUserIdTests {
		@Test
		void findByIdAndUserIdでバイクを正しく取得できること() {
			Bike bike = createBike(user, "TestMfr", "Z1", "TST-001", 2023, false);

			Optional<Bike> foundBike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(bike.getId(), user.getId());

			assertTrue(foundBike.isPresent());
			assertThat(foundBike.get().getModelName()).isEqualTo("Z1");
		}

		@Test
		void 論理削除されたバイクは取得できないこと() {
			Bike deletedBike = createBike(user, "TestMfr", "Z1", "TST-001", 2023, true);

			Optional<Bike> foundBike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(deletedBike.getId(), user.getId());

			assertFalse(foundBike.isPresent());
		}

		@Test
		void 他ユーザーのバイクは取得できないこと() {
			Bike bike = createBike(user, "TestMfr", "Z1", "TST-001", 2023, false);

			Optional<Bike> foundBike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(bike.getId(), anotherUser.getId());

			assertFalse(foundBike.isPresent());
		}
	}
}