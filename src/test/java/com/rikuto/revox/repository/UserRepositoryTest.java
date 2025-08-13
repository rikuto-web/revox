package com.rikuto.revox.repository;

import com.rikuto.revox.domain.user.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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

	@Nested
	class FindByIdTests {
		@Test
		void 有効なユーザーIDで検索が適切に行えること() {
			User testUser = createUser("testUser", "unique_id_12345");
			createUser("anotherUser", "unique_id_98765");

			Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(testUser.getId());

			assertThat(foundUser).isPresent();
			assertThat(foundUser.get().getNickname()).isEqualTo("testUser");
		}

		@Test
		void 論理削除されたユーザーは検索で取得できないこと() {
			User deletedUser = createUser("DeletedUser", "unique_id_12345");
			deletedUser.softDelete();
			userRepository.save(deletedUser);

			Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(deletedUser.getId());

			assertThat(foundUser).isNotPresent();
		}

		@Test
		void 存在しないユーザーIDで検索が失敗し空のOptionalが返ってくること() {
			Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(99999);

			assertThat(foundUser).isNotPresent();
		}
	}

	@Nested
	class FindByUniqueUserIdTests {
		@Test
		void 有効な外部認証IDで検索が適切に行えること() {
			User authIdUser = createUser("SearchUser", "unique_id_12345");

			Optional<User> foundAuthUser = userRepository.findByUniqueUserIdAndIsDeletedFalse("unique_id_12345");

			assertThat(foundAuthUser).isPresent();
			assertThat(foundAuthUser.get().getNickname()).isEqualTo("SearchUser");
		}

		@Test
		void 存在しない外部認証IDで検索が失敗し空のOptionalが返ってくること() {
			Optional<User> foundUser = userRepository.findByUniqueUserIdAndIsDeletedFalse("nonexistent_id");

			assertThat(foundUser).isNotPresent();
		}
	}

	@Nested
	class FindAllByUniqueUserIdTests {
		@Test
		void 論理削除されたユーザーを含めて外部認証IDで検索が適切に行えること() {
			User deletedUser = createUser("DeletedUser", "unique_id_deleted");
			deletedUser.softDelete();
			userRepository.save(deletedUser);

			Optional<User> foundDeletedUser = userRepository.findByUniqueUserId("unique_id_deleted");

			assertThat(foundDeletedUser).isPresent();
			assertThat(foundDeletedUser.get().isDeleted()).isTrue();
		}
	}
}