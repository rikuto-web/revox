package com.rikuto.revox.repository;

import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
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
			createBike(user, "TestBike", "Bike", "test", 2023, false);
			createBike(user, "TestSecondBike", "SecondBike", "Test", 2024, false);

			List<Bike> result = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

			assertThat(result).hasSize(2);
			assertThat(result).extracting(Bike::getModelName).containsExactlyInAnyOrder("Bike", "SecondBike");
		}

		@Test
		void 論理削除されたバイクは検索結果に含まれないこと() {
			createBike(user, "TestBike", "Bike", "test", 2023, false);
			createBike(user, "TestSecondBike", "SecondBike", "Test", 2024, true);

			List<Bike> result = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

			assertThat(result).hasSize(1);
			assertThat(result.getFirst().getModelName()).isEqualTo("Bike");
		}

		@Test
		void 別ユーザーのバイクは検索結果に含まれないこと() {
			Bike bike = createBike(user, "TestBike", "Bike", "test", 2023, false);
			createBike(anotherUser, "AnotherBike", "Another", "TestAnother", 2024, false);

			List<Bike> result = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

			assertThat(result).hasSize(1);
			assertThat(result.getFirst().getId()).isEqualTo(bike.getId());
		}

		@Test
		void 存在しないユーザーIDに対して空のリストを返すこと() {
			List<Bike> result = bikeRepository.findByUserIdAndIsDeletedFalse(999999);

			assertThat(result).isEmpty();
		}

		@Test
		void バイク未登録ユーザーには空のリストを返すこと() {
			List<Bike> result = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

			assertThat(result).isEmpty();
		}
	}

	@Nested
	class FindByIdAndUserIdTests {
		@Test
		void findByIdAndUserIdでバイクを正しく取得できること() {
			Bike bike = createBike(user, "TestBike", "Bike", "test", 2023, false);

			Optional<Bike> result = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(bike.getId(), user.getId());

			assertTrue(result.isPresent());
			assertThat(result.get().getModelName()).isEqualTo("Bike");
		}

		@Test
		void 論理削除されたバイクは取得できないこと() {
			Bike deletedBike = createBike(user, "TestBike", "Bike", "test", 2023, true);

			Optional<Bike> result = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(deletedBike.getId(), user.getId());

			assertFalse(result.isPresent());
		}

		@Test
		void 他ユーザーのバイクは取得できないこと() {
			Bike bike = createBike(user, "TestBike", "Bike", "test", 2023, false);

			Optional<Bike> result = bikeRepository.findByIdAndUserIdAndIsDeletedFalse(bike.getId(), anotherUser.getId());

			assertFalse(result.isPresent());
		}
	}
}