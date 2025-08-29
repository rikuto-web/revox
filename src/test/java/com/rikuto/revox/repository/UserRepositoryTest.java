package com.rikuto.revox.repository;

import com.rikuto.revox.domain.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
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

			Optional<User> result = userRepository.findByIdAndIsDeletedFalse(testUser.getId());

			assertThat(result).isPresent();
			assertThat(result.get().getNickname()).isEqualTo("testUser");
		}

		@Test
		void 論理削除されたユーザーは検索で取得できないこと() {
			User deletedUser = createUser("DeletedUser", "unique_id_12345");
			deletedUser.softDelete();
			userRepository.save(deletedUser);

			Optional<User> result = userRepository.findByIdAndIsDeletedFalse(deletedUser.getId());

			assertThat(result).isNotPresent();
		}

		@Test
		void 存在しないユーザーIDで検索が失敗し空のOptionalが返ってくること() {
			Optional<User> result = userRepository.findByIdAndIsDeletedFalse(99999);

			assertThat(result).isNotPresent();
		}
	}

	@Nested
	class FindByUniqueUserIdTests {
		@Test
		void 有効な外部認証IDで検索が適切に行えること() {
			createUser("SearchUser", "unique_id_12345");

			Optional<User> result = userRepository.findByUniqueUserIdAndIsDeletedFalse("unique_id_12345");

			assertThat(result).isPresent();
			assertThat(result.get().getNickname()).isEqualTo("SearchUser");
		}

		@Test
		void 存在しない外部認証IDで検索が失敗し空のOptionalが返ってくること() {
			Optional<User> result = userRepository.findByUniqueUserIdAndIsDeletedFalse("nonexistent_id");

			assertThat(result).isNotPresent();
		}
	}

	@Nested
	class FindAllByUniqueUserIdTests {
		@Test
		void 論理削除されたユーザーを含めて外部認証IDで検索が適切に行えること() {
			User deletedUser = createUser("DeletedUser", "unique_id_deleted");
			deletedUser.softDelete();
			userRepository.save(deletedUser);

			Optional<User> result = userRepository.findByUniqueUserId("unique_id_deleted");

			assertThat(result).isPresent();
			assertThat(result.get().isDeleted()).isTrue();
		}
	}
}