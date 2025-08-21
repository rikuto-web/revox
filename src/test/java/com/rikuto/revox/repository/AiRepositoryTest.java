package com.rikuto.revox.repository;

import com.rikuto.revox.domain.Ai;
import com.rikuto.revox.domain.Category;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AiRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BikeRepository bikeRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private AiRepository aiRepository;

	private User createUser(String nickname) {
		return userRepository.save(User.builder()
				.nickname(nickname)
				.uniqueUserId(java.util.UUID.randomUUID().toString())
				.build());
	}

	private Bike createBike(User user, String manufacturer, String modelName) {
		return bikeRepository.save(Bike.builder()
				.user(user)
				.manufacturer(manufacturer)
				.modelName(modelName)
				.build());
	}

	private Category createCategory(String name, Integer displayOrder) {
		return categoryRepository.save(Category.builder()
				.name(name)
				.displayOrder(displayOrder)
				.build());
	}

	private Ai createAiConversation(User user, Bike bike, Category category, String question, String answer) {
		return aiRepository.save(Ai.builder()
				.user(user)
				.bike(bike)
				.category(category)
				.question(question)
				.answer(answer)
				.build());
	}

	@Test
	void ユーザーIDに紐づくAI質問履歴を正しく取得できること() {
		User user = createUser("TestUser");
		Bike bike = createBike(user, "TestBike", "Test");
		Category category = createCategory("TestCategory", 1);

		createAiConversation(user, bike, category, "質問1", "回答1");
		createAiConversation(user, bike, category, "質問2", "回答2");

		List<Ai> result = aiRepository.findByUserId(user.getId());

		assertThat(result).hasSize(2);
		assertThat(result).extracting(Ai::getQuestion)
				.containsExactlyInAnyOrder("質問1", "質問2");
	}

	@Test
	void 別ユーザーのAI質問履歴は検索結果に含まれないこと() {
		User user = createUser("Owner");
		Bike bike = createBike(user, "OwnerBike", "Owner");
		Category category = createCategory("TestCategory", 1);
		Ai aiConversation = createAiConversation(user, bike, category, "オーナーの質問", "オーナーの回答");

		User anotherUser = createUser("Another");
		Bike anotherbike = createBike(anotherUser, "AnotherBike", "Another");
		createAiConversation(anotherUser, anotherbike, category, "別ユーザーの質問", "別ユーザーの回答");

		List<Ai> result = aiRepository.findByUserId(user.getId());

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo(aiConversation.getId());
	}

	@Test
	void 存在しないユーザーIDに対して空のリストを返すこと() {
		List<Ai> result = aiRepository.findByUserId(9999);

		assertThat(result).isEmpty();
	}

	@Test
	void AI質問履歴がないユーザーには空のリストを返すこと() {
		User user = createUser("EmptyAnswer");

		List<Ai> result = aiRepository.findByUserId(user.getId());

		assertThat(result).isEmpty();
	}

	@Test
	void 複数のバイクとカテゴリーに関するAI質問履歴を正しく取得できること() {
		User owner = createUser("Owner");

		Bike firseBike = createBike(owner, "firseBike", "TestBike");
		Bike secondBike = createBike(owner, "secondBike", "BikeTest");

		Category categoryEngine = createCategory("Engine", 1);
		Category categoryBrake = createCategory("Brake", 2);

		createAiConversation(owner, firseBike, categoryEngine, "バイク1エンジン質問", "バイク1エンジン回答");
		createAiConversation(owner, secondBike, categoryBrake, "バイク2ブレーキ質問", "バイク2ブレーキ回答");

		List<Ai> result = aiRepository.findByUserId(owner.getId());

		assertThat(result).hasSize(2);
		assertThat(result).extracting(Ai::getQuestion)
				.containsExactlyInAnyOrder("バイク1エンジン質問", "バイク2ブレーキ質問");
	}
}