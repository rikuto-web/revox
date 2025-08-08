package com.rikuto.revox.repository;

import com.rikuto.revox.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * UserRepositoryのテストクラスです。
 */
@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	private User createUser(String nickname, String uniqueId) {
		return userRepository.save(User.builder()
				.uniqueUserId(uniqueId)
				.nickname(nickname)
				.build());
	}

	@Test
	void IDでの検索が適切に行えていること() {
		User testUser = createUser("testUser", "unique_id_12345");
		User anotherUser = createUser("anotherUser", "unique_id_98765");
		userRepository.save(testUser);

		Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(testUser.getId());

		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getNickname()).isEqualTo("testUser");
		assertThat(foundUser.get().getUniqueUserId()).isEqualTo("unique_id_12345");
	}

	@Test
	void 外部認証IDでの検索が適切に行えていること() {
		User testGoogleIdUser = User.builder()
				.nickname("SearchUser")
				.uniqueUserId("unique_id_12345")
				.build();
		userRepository.save(testGoogleIdUser);

		Optional<User> foundUser = userRepository.findByUniqueUserIdAndIsDeletedFalse("unique_id_12345");

		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getNickname()).isEqualTo("SearchUser");
		assertThat(foundUser.get().getUniqueUserId()).isEqualTo("unique_id_12345");
	}

	@Test
	void 論理削除されたユーザーはID検索で取得できないこと() {
		User deletedUser = User.builder()
				.uniqueUserId("unique_id_12345")
				.nickname("DeletedUser")
				.isDeleted(true)
				.build();
		userRepository.save(deletedUser);

		Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(deletedUser.getId());

		assertThat(foundUser).isNotPresent();
	}

	@Test
	void 存在しないユーザーIDでの検索が失敗し空のOptionalが返ってくること() {
		Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(99999);

		assertThat(foundUser).isNotPresent();
	}

	@Test
	void 外部認証IDでの検索が失敗し空のOptionalが返ってくること() {
		Optional<User> foundUser = userRepository.findByUniqueUserIdAndIsDeletedFalse("nonexistent_id");

		assertThat(foundUser).isNotPresent();
	}

	@Test
	void 同じ外部認証IDのユーザーを登録しようとするとDataIntegrityViolationExceptionを投げること() {
		User user1 = User.builder()
				.nickname("UserA")
				.uniqueUserId("duplicate_id_for_google")
				.build();
		userRepository.save(user1);

		User user2 = User.builder()
				.nickname("UserB")
				.uniqueUserId("duplicate_id_for_google") // 重複するID
				.build();

		assertThatThrownBy(() -> userRepository.save(user2))
				.isInstanceOf(DataIntegrityViolationException.class);
	}
}