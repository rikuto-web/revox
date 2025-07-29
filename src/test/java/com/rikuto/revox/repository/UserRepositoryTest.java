package com.rikuto.revox.repository;

import com.rikuto.revox.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * UserRepositoryのテストクラスです。
 * JpaRepositoryを継承しているため独自メソッドのみテストを行っています。
 */
@DataJpaTest
class UserRepositoryTest{

	@Autowired
	private UserRepository userRepository;

	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();
	}

	@Test
	void IDでの検索が適切に行えていること() {
		// Given ユーザーのダミーデータの準備
		User testUser = User.builder()
				.nickname("IdSearchUser")
				.createdAt(now)
				.updatedAt(now)
				.build();
		userRepository.save(testUser);

		// When　IDでの検索
		Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(testUser.getId());

		// Then　登録内容と検索結果が一致するか検証
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getNickname()).isEqualTo("IdSearchUser");
	}

	@Test
	void メールアドレスでの検索が適切に行えていること(){
		// Given Email登録ユーザーのダミーデータの準備
		User testEmailUser = User.builder()
				.nickname("EmailSearchUser")
				.email("test.user@example.com")
				.createdAt(now)
				.updatedAt(now)
				.build();
		userRepository.save(testEmailUser);

		// When　Emailでの検索
		Optional<User> foundUser = userRepository.findByEmailAndIsDeletedFalse("test.user@example.com");

		// Then　登録内容と検索結果が一致するか検証
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getNickname()).isEqualTo("EmailSearchUser");
		assertThat(foundUser.get().getEmail()).isEqualTo("test.user@example.com");
	}

	@Test
	void グーグルIDでの検索が適切に行えていること(){
		// Given　Google認証登録ユーザーのダミーデータの準備
		User testGoogleIdUser = User.builder()
				.nickname("GoogleSearchUser")
				.googleId("unique_google_id_12345")
				.createdAt(now)
				.updatedAt(now)
				.build();
		userRepository.save(testGoogleIdUser);

		// When　GoogleIDでの検索
		Optional<User> foundUser = userRepository.findByGoogleIdAndIsDeletedFalse("unique_google_id_12345");

		// Then　登録内容と検索結果が一致するか検証
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getNickname()).isEqualTo("GoogleSearchUser");
		assertThat(foundUser.get().getGoogleId()).isEqualTo("unique_google_id_12345");
	}

	@Test
	void ラインIDでの検索が適切に行えていること(){
		// Given　Line認証登録ユーザーのダミーデータの準備
		User testLineIdUser = User.builder()
				.nickname("LineSearchUser")
				.lineId("unique_line_id_abcde")
				.createdAt(now)
				.updatedAt(now)
				.build();
		userRepository.save(testLineIdUser);

		// When　LineIDでの検索
		Optional<User> foundUser = userRepository.findByLineIdAndIsDeletedFalse("unique_line_id_abcde");

		// Then　登録内容と検索結果が一致するか検証
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getNickname()).isEqualTo("LineSearchUser");
		assertThat(foundUser.get().getLineId()).isEqualTo("unique_line_id_abcde");
	}


	@Test
	void 論理削除されたユーザーはID検索で取得できないこと() {
		User deletedUser = User.builder()
				.nickname("DeletedUser")
				.isDeleted(true)
				.createdAt(now)
				.updatedAt(now)
				.build();
		userRepository.save(deletedUser);

		Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(deletedUser.getId());

		assertThat(foundUser).isNotPresent();
	}

	@Test
	void メールアドレスでの検索が失敗し空のOptionalが返ってくること(){
		// Given
		// When
		Optional<User> foundUser = userRepository.findByEmailAndIsDeletedFalse("nonexistent.user@example.com");

		// Then
		assertThat(foundUser).isNotPresent();
	}

	@Test
	void グーグルIDでの検索が失敗し空のOptionalが返ってくること(){
		// Given
		// When
		Optional<User> foundUser = userRepository.findByGoogleIdAndIsDeletedFalse("nonexistent_google_id");

		// Then
		assertThat(foundUser).isNotPresent();
	}

	@Test
	void ラインIDでの検索が失敗し空のOptionalが返ってくること(){
		// Given
		// When
		Optional<User> foundUser = userRepository.findByLineIdAndIsDeletedFalse("nonexistent_line_id");

		// Then
		assertThat(foundUser).isNotPresent();
	}

	@Test
	void 同じGoogleIDのユーザーを登録しようとするとDataIntegrityViolationExceptionを投げること() {
		// Given: 最初のユーザー（Google ID）を保存し、成功させる
		User user1 = User.builder()
				.nickname("UserA")
				.googleId("duplicate_id_for_google")
				.createdAt(now)
				.updatedAt(now)
				.build();
		userRepository.save(user1);

		// When & Then: 同じGoogle IDを持つ別のユーザーを保存しようとするとDataIntegrityViolationExceptionがスローされること
		User user2 = User.builder()
				.nickname("UserB")
				.googleId("duplicate_id_for_google") // 重複するID
				.createdAt(now.plusMinutes(1))
				.updatedAt(now.plusMinutes(1))
				.build();

		assertThatThrownBy(() -> userRepository.save(user2))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void 同じLineIDのユーザーを登録しようとするとDataIntegrityViolationExceptionを投げること() {
		// Given: 最初のユーザー（Line ID）を保存し、成功させる
		User user3 = User.builder()
				.nickname("UserC")
				.lineId("duplicate_id_for_line")
				.createdAt(now)
				.updatedAt(now)
				.build();
		userRepository.save(user3);

		// When & Then: 同じLine IDを持つ別のユーザーを保存しようとするとDataIntegrityViolationExceptionがスローされること
		User user4 = User.builder()
				.nickname("UserD")
				.lineId("duplicate_id_for_line") // 重複するID
				.createdAt(now.plusMinutes(1))
				.updatedAt(now.plusMinutes(1))
				.build();

		assertThatThrownBy(() -> userRepository.save(user4))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void 登録されたユーザーのニックネームが50文字を超えた場合DataIntegrityViolationExceptionを投げること() {
		// Given
		String longNickname = "a".repeat(51);
		User invalidUser = User.builder()
				.nickname(longNickname)
				.email("too.long.nickname@example.com")
				.createdAt(now)
				.updatedAt(now)
				.build();

		// When & Then
		assertThatThrownBy(() -> userRepository.save(invalidUser))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void ニックネームがnullの場合DataIntegrityViolationExceptionを投げること() {
		// Given
		User invalidUser = User.builder()
				.nickname(null)
				.email("test@example.com")
				.createdAt(now)
				.updatedAt(now)
				.build();

		// When & Then
		assertThatThrownBy(() -> userRepository.save(invalidUser))
				.isInstanceOf(DataIntegrityViolationException.class);
	}
}