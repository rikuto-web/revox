package com.rikuto.revox.repository;

import com.rikuto.revox.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
		userRepository.save(testUser);

		User anotherUser = createUser("anotherUser", "unique_id_98765");
		userRepository.save(anotherUser);

		Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(testUser.getId());

		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getNickname()).isEqualTo("testUser");
		assertThat(foundUser.get().getUniqueUserId()).isEqualTo("unique_id_12345");
	}

	@Test
	void 外部認証IDでの検索が適切に行えていること() {
		User authIdUser = createUser("SearchUser", "unique_id_12345");
		userRepository.save(authIdUser);

		Optional<User> foundAuthUser = userRepository.findByUniqueUserIdAndIsDeletedFalse("unique_id_12345");

		assertThat(foundAuthUser).isPresent();
		assertThat(foundAuthUser.get().getNickname()).isEqualTo("SearchUser");
		assertThat(foundAuthUser.get().getUniqueUserId()).isEqualTo("unique_id_12345");
	}

	@Test
	void 論理削除されたユーザーはID検索で取得できないこと() {
		User deleteUser = User.builder()
				.uniqueUserId("unique_id_12345")
				.nickname("DeletedUser")
				.isDeleted(true)
				.build();
		userRepository.save(deleteUser);

		Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(deleteUser.getId());

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
}