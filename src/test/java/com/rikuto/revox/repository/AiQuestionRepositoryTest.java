package com.rikuto.revox.repository;

import com.rikuto.revox.domain.AiQuestion;
import com.rikuto.revox.domain.user.User;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
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

	private AiQuestion createAiQuestion(User user, Bike bike, Category category, String question, String answer, boolean isDeleted) {
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

		createAiQuestion(user, bike, category, "質問1", "回答1", false);
		createAiQuestion(user, bike, category, "質問2", "回答2", false);
		createAiQuestion(user, bike, category, "質問3", "回答3", true);

		List<AiQuestion> aiQuestions = aiQuestionRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(aiQuestions).hasSize(2);
		assertThat(aiQuestions).extracting(AiQuestion::getQuestion)
				.containsExactlyInAnyOrder("質問1", "質問2");
	}

	@Test
	void 別ユーザーのAI質問履歴は検索結果に含まれないこと() {
		User owner = createUser("User1");
		Bike ownersBike = createBike(owner, "Honda", "CBR250RR");
		Category category = createCategory("TestCategory", 999);
		AiQuestion ownersQuestion = createAiQuestion(owner, ownersBike, category, "オーナーの質問", "オーナーの回答", false);

		User anotherUser = createUser("User2");
		Bike anotherbike = createBike(anotherUser, "Yamaha", "YZF-R1");
		createAiQuestion(anotherUser, anotherbike, category, "別ユーザーの質問", "別ユーザーの回答", false);

		List<AiQuestion> aiQuestions = aiQuestionRepository.findByUserIdAndIsDeletedFalse(owner.getId());

		assertThat(aiQuestions).hasSize(1);
		assertThat(aiQuestions.getFirst().getId()).isEqualTo(ownersQuestion.getId());
	}

	@Test
	void 存在しないユーザーIDに対して空のリストを返すこと(){

		List<AiQuestion> aiQuestions = aiQuestionRepository.findByUserIdAndIsDeletedFalse(9999);

		assertThat(aiQuestions).isEmpty();
	}

	@Test
	void AI質問履歴がないユーザーには空のリストを返すこと(){

		User owner = createUser("EmptyUser");

		List<AiQuestion> aiQuestions = aiQuestionRepository.findByUserIdAndIsDeletedFalse(owner.getId());

		assertThat(aiQuestions).isEmpty();
	}

	@Test
	void 論理削除されたAI質問は検索結果に含まれないこと(){

		User user = createUser("User");
		Bike bike = createBike(user, "Honda", "CBR250RR");
		Category category = createCategory("TestCategory", 999);

		createAiQuestion(user, bike, category, "アクティブな質問", "アクティブな回答", false);
		createAiQuestion(user, bike, category, "削除された質問", "削除された回答", true);

		List<AiQuestion> aiQuestions = aiQuestionRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(aiQuestions).hasSize(1);
		assertThat(aiQuestions.getFirst().getQuestion()).isEqualTo("アクティブな質問");
	}

	@Test
	void 複数のバイクとカテゴリーに関するAI質問履歴を正しく取得できること(){

		User user = createUser("MultiUser");

		Bike bike1 = createBike(user, "Honda", "CBR250RR");
		Bike bike2 = createBike(user, "Yamaha", "YZF-R1");

		Category category1 = createCategory("Engine", 1);
		Category category2 = createCategory("Brake", 2);

		createAiQuestion(user, bike1, category1, "バイク1エンジン質問", "バイク1エンジン回答", false);
		createAiQuestion(user, bike2, category2, "バイク2ブレーキ質問", "バイク2ブレーキ回答", false);

		List<AiQuestion> aiQuestions = aiQuestionRepository.findByUserIdAndIsDeletedFalse(user.getId());

		assertThat(aiQuestions).hasSize(2);
		assertThat(aiQuestions).extracting(AiQuestion::getQuestion)
				.containsExactlyInAnyOrder("バイク1エンジン質問", "バイク2ブレーキ質問");
	}
}