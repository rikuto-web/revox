package com.rikuto.revox.repository;

import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.domain.bike.Bike;
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

	@Test
	void ユーザーIDに紐づくバイク情報を正しく取得できること() {
		User user = createUser("TestUser");

		createBike(user, "TestMfr", "Z1", "TST-001", 2023, false);
		createBike(user, "TestTestMfr", "Z2", "TST-002", 2024, false);
		createBike(user, "TestTestTestMfr", "cb400f", "TST-003", 2020, true);

		List<Bike> bikeList = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(bikeList).hasSize(2);
		assertThat(bikeList).extracting(Bike::getModelName)
				.containsExactlyInAnyOrder("Z1", "Z2");
	}

	@Test
	void 別ユーザーのバイクは検索結果に含まれないこと() {
		User user = createUser("User1");
		Bike bike = createBike(user, "TestMfr", "Z1", "TST-001", 2023, false);

		User anotherUser = createUser("User2");
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
		User user = createUser("EmptyUser");

		List<Bike> bikeList = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(bikeList).isEmpty();
	}

	@Test
	void 論理削除されたバイクは検索結果に含まれないこと() {
		User user = createUser("User");

		createBike(user, "TestMfr", "Z1", "TST-001", 2023, false);
		createBike(user, "TestTestMfr", "Z2", "TST-002", 2024, true);

		List<Bike> bikeList = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(bikeList).hasSize(1);
		assertThat(bikeList.getFirst().getModelName()).isEqualTo("Z1");
	}

	@Test
	void findByIdAndUserIdでバイクを正しく取得できること() {
		User user = createUser("FindUser");
		Bike bike = createBike(user, "TestMfr", "Z1", "TST-001", 2023, false);

		Optional<Bike> foundBike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(user.getId(), bike.getId());

		assertTrue(foundBike.isPresent());
		assertThat(foundBike.get().getModelName()).isEqualTo("Z1");
	}

	@Test
	void findByIdAndUserIdで論理削除されたバイクは取得できないこと() {
		User user = createUser("DeletedFindUser");
		Bike deletedBike = createBike(user, "TestMfr", "Z1", "TST-001", 2023, true);

		Optional<Bike> foundBike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(deletedBike.getId(), user.getId());

		assertFalse(foundBike.isPresent());
	}

	@Test
	void findByIdAndUserIdで他ユーザーのバイクは取得できないこと() {
		User user = createUser("User1");
		Bike bike = createBike(user, "TestMfr", "Z1", "TST-001", 2023, false);

		User anotherUser = createUser("User2");

		Optional<Bike> foundBike = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(bike.getId(), anotherUser.getId());

		assertFalse(foundBike.isPresent());
	}
}