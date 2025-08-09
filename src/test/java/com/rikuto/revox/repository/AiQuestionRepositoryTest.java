package com.rikuto.revox.repository;

import com.rikuto.revox.domain.AiQuestion;
import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AiQuestionRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BikeRepository bikeRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private AiQuestionRepository aiQuestionRepository;

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

	private AiQuestion createAiConversation(User user, Bike bike, Category category, String question, String answer, boolean isDeleted) {
		return aiQuestionRepository.save(AiQuestion.builder()
				.user(user)
				.bike(bike)
				.category(category)
				.question(question)
				.answer(answer)
				.isDeleted(isDeleted)
				.build());
	}

	@Test
	void ユーザーIDに紐づくAI質問履歴を正しく取得できること() {
		User user = createUser("TestUser");
		Bike bike = createBike(user, "Honda", "CBR250RR");
		Category category = createCategory("TestCategory", 999);

		createAiConversation(user, bike, category, "質問1", "回答1", false);
		createAiConversation(user, bike, category, "質問2", "回答2", false);
		createAiConversation(user, bike, category, "質問3", "回答3", true);

		List<AiQuestion> aiConversationList = aiQuestionRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(aiConversationList).hasSize(2);
		assertThat(aiConversationList).extracting(AiQuestion::getQuestion)
				.containsExactlyInAnyOrder("質問1", "質問2");
	}

	@Test
	void 別ユーザーのAI質問履歴は検索結果に含まれないこと() {
		User user = createUser("User1");
		Bike bike = createBike(user, "Honda", "CBR250RR");
		Category category = createCategory("TestCategory", 999);
		AiQuestion aiConversation = createAiConversation(user, bike, category, "オーナーの質問", "オーナーの回答", false);

		User anotherUser = createUser("User2");
		Bike anotherbike = createBike(anotherUser, "Yamaha", "YZF-R1");
		createAiConversation(anotherUser, anotherbike, category, "別ユーザーの質問", "別ユーザーの回答", false);

		List<AiQuestion> aiConversationList = aiQuestionRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(aiConversationList).hasSize(1);
		assertThat(aiConversationList.getFirst().getId()).isEqualTo(aiConversation.getId());
	}

	@Test
	void 存在しないユーザーIDに対して空のリストを返すこと(){
		List<AiQuestion> aiConversation = aiQuestionRepository.findByUserIdAndIsDeletedFalse(9999);

		assertThat(aiConversation).isEmpty();
	}

	@Test
	void AI質問履歴がないユーザーには空のリストを返すこと(){
		User user = createUser("EmptyUser");

		List<AiQuestion> aiConversation = aiQuestionRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(aiConversation).isEmpty();
	}

	@Test
	void 論理削除されたAI質問は検索結果に含まれないこと(){
		User user = createUser("User");
		Bike bike = createBike(user, "Honda", "CBR250RR");
		Category category = createCategory("TestCategory", 999);

		createAiConversation(user, bike, category, "アクティブな質問", "アクティブな回答", false);
		createAiConversation(user, bike, category, "削除された質問", "削除された回答", true);

		List<AiQuestion> aiConversation = aiQuestionRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(aiConversation).hasSize(1);
		assertThat(aiConversation.getFirst().getQuestion()).isEqualTo("アクティブな質問");
	}

	@Test
	void 複数のバイクとカテゴリーに関するAI質問履歴を正しく取得できること(){
		User user = createUser("MultiUser");

		Bike firseBike = createBike(user, "Honda", "CBR250RR");
		Bike secondBike = createBike(user, "Yamaha", "YZF-R1");

		Category categoryEngine = createCategory("Engine", 1);
		Category categoryBrake = createCategory("Brake", 2);

		createAiConversation(user, firseBike, categoryEngine, "バイク1エンジン質問", "バイク1エンジン回答", false);
		createAiConversation(user, secondBike, categoryBrake, "バイク2ブレーキ質問", "バイク2ブレーキ回答", false);

		List<AiQuestion> aiConversation = aiQuestionRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(aiConversation).hasSize(2);
		assertThat(aiConversation).extracting(AiQuestion::getQuestion)
				.containsExactlyInAnyOrder("バイク1エンジン質問", "バイク2ブレーキ質問");
	}
}