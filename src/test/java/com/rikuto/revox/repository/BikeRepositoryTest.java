package com.rikuto.revox.repository;

import com.rikuto.revox.entity.Bike;
import com.rikuto.revox.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BikeRepositoryのテストクラスです。
 * JpaRepositoryを継承しているため独自メソッドのみテストを行っています。
 */
@DataJpaTest
class BikeRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BikeRepository bikeRepository;

	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();
	}

	@Test
	void 指定されたユーザーIDに紐づくバイク情報を正しく取得できること() {
		// Given: テストユーザーとバイクデータの準備
		User testUser = User.builder()
				.nickname("TestUser")
				.email("test.user@example.com")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		// テストユーザーを保存し、生成されたIDを取得
		User savedUser = userRepository.save(testUser);
		Integer testUserId = savedUser.getId();

		//テストユーザーは1台のバイクを保有
		Bike testFirstBike = Bike.builder()
				.user(savedUser)
				.manufacturer("KAWASAKI")
				.modelName("Z1")
				.modelCode("Z1-900")
				.modelYear(1972)
				.createdAt(now)
				.updatedAt(now)
				.build();

		// 意図しないデータが取得されないことを確認するため、別ユーザーのバイクも作成
		User anotherUser = User.builder()
				.nickname("AnotherUser")
				.email("another.user@example.com")
				.createdAt(now)
				.updatedAt(now)
				.build();
		User savedAnotherUser = userRepository.save(anotherUser);

		Bike anotherUserBike = Bike.builder()
				.user(savedAnotherUser)
				.manufacturer("YAMAHA")
				.modelName("SR400")
				.modelCode("2H6")
				.modelYear(1978)
				.createdAt(now)
				.updatedAt(now)
				.build();

		//2台のバイク情報を保存
		bikeRepository.save(testFirstBike);
		bikeRepository.save(anotherUserBike);

		// When: 指定されたユーザーIDに紐づくバイク情報を検索
		Optional<Bike> bikeForTestUser = bikeRepository.findByUserIdAndIsDeletedFalse(testUserId);

		// Then: 正しいバイク情報のみが取得されていることを確認
		assertTrue(bikeForTestUser.isPresent(), "テストユーザーのバイクが見つかるはずです");
		Bike foundBike = bikeForTestUser.get();
		assertThat(foundBike.getModelName()).isEqualTo("Z1");
		assertThat(foundBike.getManufacturer()).isEqualTo("KAWASAKI");
		assertThat(foundBike.getUser().getId()).isEqualTo(testUserId);
	}

	@Test
	void 存在しないユーザーIDに対して空のOptionalを返すこと() {
		// When: 存在しないユーザーIDに紐づくバイク情報を検索
		Optional<Bike> bikesForNonExistentUser = bikeRepository.findByUserIdAndIsDeletedFalse(999999999);

		// Then: ユーザーが見つからず空のリストが返ってくる
		assertFalse(bikesForNonExistentUser.isPresent(), "存在しないユーザーIDに対してはバイクが見つからないはずです");
		assertThat(bikesForNonExistentUser).isEmpty();
	}

	@Test
	void バイク未登録ユーザーは空のOptionalを返すこと() {
		// Given: テストユーザーの準備
		User testUser = userRepository.save(User.builder()
				.nickname("EmptyUser")
				.email("empty@example.com")
				.createdAt(now)
				.updatedAt(now)
				.build());
		Integer testUserId = testUser.getId();

		// When: 存在するユーザーＩＤに紐づくバイク情報を検索
		Optional<Bike> emptyBikes = bikeRepository.findByUserIdAndIsDeletedFalse(testUserId);

		// Then: バイクが見つからず空のリストが返ってくる
		assertFalse(emptyBikes.isPresent(), "バイク未登録ユーザーに対してはバイクが見つからないはずです");
		assertThat(emptyBikes).isEmpty();
	}

	@Test
	void 論理削除されたバイクは検索結果に含まれないこと() {
		// Given: テストユーザーと、アクティブなバイク、論理削除されたバイクの準備
		User testUser = userRepository.save(User.builder()
				.nickname("DeleteTestUser")
				.email("delete.test@example.com")
				.createdAt(now)
				.updatedAt(now)
				.build());

		Bike activeBike = Bike.builder()
				.user(testUser)
				.manufacturer("ActiveMfr")
				.modelName("ActiveModel")
				.createdAt(now)
				.updatedAt(now)
				.build();
		bikeRepository.save(activeBike);

		Bike softDeletedBike = Bike.builder()
				.user(testUser)
				.manufacturer("DeletedMfr")
				.modelName("DeletedModel")
				.isDeleted(true)
				.createdAt(now.minusDays(1))
				.updatedAt(now.minusDays(1))
				.build();
		bikeRepository.save(softDeletedBike);

		// When: ユーザーIDでバイクを検索
		Optional<Bike> foundBikes = bikeRepository.findByUserIdAndIsDeletedFalse(testUser.getId());

		// Then: アクティブなバイクのみが取得され、論理削除されたバイクは含まれないことを確認
		assertTrue(foundBikes.isPresent(), "アクティブなバイクが見つかるはずです");
		Bike foundBike = foundBikes.get();
		assertThat(foundBike.getModelName()).isEqualTo("ActiveModel");
		assertThat(foundBike.getModelName()).isNotEqualTo("DeletedModel"); // 論理削除されたバイクでないことを確認
	}

	@Test
	void 指定したユーザーIDでアクティブなバイクを正しく取得できること() {
		// Given
		User user = userRepository.save(User.builder()
				.nickname("UserForIdTest")
				.email("id.test@example.com")
				.createdAt(now)
				.updatedAt(now)
				.build());

		Bike bike = Bike.builder()
				.user(user)
				.manufacturer("TestMfr")
				.modelName("TestModel")
				.createdAt(now)
				.updatedAt(now)
				.build();
		Bike savedBike = bikeRepository.save(bike);

		// When
		Optional<Bike> foundOptional = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

		// Then
		assertTrue(foundOptional.isPresent());
		assertThat(foundOptional.get().getId()).isEqualTo(savedBike.getId());
		assertThat(foundOptional.get().getModelName()).isEqualTo("TestModel");
	}

	@Test
	void 指定したユーザーIDで論理削除されたバイクは取得できないこと() {
		// Given
		User user = userRepository.save(User.builder()
				.nickname("UserForDeletedIdTest")
				.email("deleted.id.test@example.com")
				.createdAt(now)
				.updatedAt(now)
				.build());

		Bike bike = Bike.builder()
				.user(user)
				.manufacturer("DeletedTestMfr")
				.modelName("DeletedTestModel")
				.isDeleted(true) // 論理削除済み
				.createdAt(now)
				.updatedAt(now)
				.build();
		Bike savedBike = bikeRepository.save(bike);

		// When
		Optional<Bike> foundOptional = bikeRepository.findByUserIdAndIsDeletedFalse(user.getId());

		// Then
		assertFalse(foundOptional.isPresent());
	}
}