package com.rikuto.revox.repository;

import com.rikuto.revox.entity.Bike;
import com.rikuto.revox.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

		//テストユーザーは２台のバイクを保有
		Bike testFirstBike = Bike.builder()
				.user(savedUser)
				.manufacturer("KAWASAKI")
				.modelName("Z1")
				.modelCode("Z1-900")
				.modelYear(1972)
				.createdAt(now)
				.updatedAt(now)
				.build();

		Bike testSecondBike = Bike.builder()
				.user(savedUser)
				.manufacturer("HONDA")
				.modelName("CB400F")
				.modelCode("NC36")
				.modelYear(1975)
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

		//３台のバイク情報を保存
		bikeRepository.save(testFirstBike);
		bikeRepository.save(testSecondBike);
		bikeRepository.save(anotherUserBike);

		// When: 指定されたユーザーIDに紐づくバイク情報を検索
		List<Bike> bikeForTestUser = bikeRepository.findByUserIdAndIsDeletedFalse(testUserId);

		// Then: 正しいバイク情報のみが取得されていることを確認
		assertThat(bikeForTestUser).isNotNull();
		assertThat(bikeForTestUser).hasSize(2);
		assertThat(bikeForTestUser)
				.extracting(Bike::getModelName)
				.containsExactlyInAnyOrder("Z1", "CB400F");
	}

	@Test
	void 存在しないユーザーIDに対して空のリストを返すこと() {
		// When: 存在しないユーザーIDに紐づくバイク情報を検索
		List<Bike> bikesForNonExistentUser = bikeRepository.findByUserIdAndIsDeletedFalse(999999999);

		// Then: ユーザーが見つからず空のリストが返ってくる
		assertThat(bikesForNonExistentUser).isEmpty();
	}

	@Test
	void バイク未登録ユーザーは空のリストを返すこと() {
		// Given: テストユーザーの準備
		User testUser = userRepository.save(User.builder()
				.nickname("EmptyUser")
				.email("empty@example.com")
				.createdAt(now)
				.updatedAt(now)
				.build());
		User savedUser = userRepository.save(testUser);
		Integer testUserId = savedUser.getId();

		// When: 存在するユーザーＩＤに紐づくバイク情報を検索
		List<Bike> emptyBikes = bikeRepository.findByUserIdAndIsDeletedFalse(testUserId);

		// Then: バイクが見つからず空のリストが返ってくる
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
		List<Bike> foundBikes = bikeRepository.findByUserIdAndIsDeletedFalse(testUser.getId());

		// Then: アクティブなバイクのみが取得され、論理削除されたバイクは含まれないことを確認
		assertThat(foundBikes).hasSize(1);
		assertThat(foundBikes).extracting(Bike::getModelName).containsExactly("ActiveModel");
		assertThat(foundBikes).extracting(Bike::getModelName).doesNotContain("DeletedModel");
	}
}