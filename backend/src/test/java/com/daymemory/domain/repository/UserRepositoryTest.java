package com.daymemory.domain.repository;

import com.daymemory.domain.entity.OAuthProvider;
import com.daymemory.domain.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword123")
                .nickname("Test User")
                .emailVerified(false)
                .build();
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void testFindByEmail_Success() {
        // given
        User savedUser = userRepository.save(testUser);

        // when
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getNickname()).isEqualTo("Test User");
        assertThat(foundUser.get().getEmailVerified()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 빈 Optional 반환")
    void testFindByEmail_NotFound() {
        // given
        userRepository.save(testUser);

        // when
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("이메일 존재 확인 - 존재하는 경우")
    void testExistsByEmail_True() {
        // given
        userRepository.save(testUser);

        // when
        boolean exists = userRepository.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 확인 - 존재하지 않는 경우")
    void testExistsByEmail_False() {
        // given
        userRepository.save(testUser);

        // when
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("OAuth Provider와 ProviderId로 사용자 조회 성공")
    void testFindByOauthProviderAndOauthProviderId_Success() {
        // given
        User oauthUser = User.builder()
                .email("oauth@example.com")
                .password("dummy")
                .nickname("OAuth User")
                .oauthProvider(OAuthProvider.GOOGLE)
                .oauthProviderId("google-12345")
                .emailVerified(true)
                .build();
        userRepository.save(oauthUser);

        // when
        Optional<User> foundUser = userRepository.findByOauthProviderAndOauthProviderId(
                OAuthProvider.GOOGLE, "google-12345");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("oauth@example.com");
        assertThat(foundUser.get().getOauthProvider()).isEqualTo(OAuthProvider.GOOGLE);
        assertThat(foundUser.get().getOauthProviderId()).isEqualTo("google-12345");
    }

    @Test
    @DisplayName("OAuth Provider와 ProviderId로 사용자 조회 실패 - 존재하지 않는 경우")
    void testFindByOauthProviderAndOauthProviderId_NotFound() {
        // when
        Optional<User> foundUser = userRepository.findByOauthProviderAndOauthProviderId(
                OAuthProvider.GOOGLE, "nonexistent-id");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("이메일 인증 상태 업데이트 테스트")
    void testVerifyEmail() {
        // given
        User savedUser = userRepository.save(testUser);
        assertThat(savedUser.getEmailVerified()).isFalse();

        // when
        savedUser.verifyEmail();
        User updatedUser = userRepository.save(savedUser);

        // then
        assertThat(updatedUser.getEmailVerified()).isTrue();
    }

    @Test
    @DisplayName("비밀번호 업데이트 테스트")
    void testUpdatePassword() {
        // given
        User savedUser = userRepository.save(testUser);
        String oldPassword = savedUser.getPassword();

        // when
        savedUser.updatePassword("newEncodedPassword456");
        User updatedUser = userRepository.save(savedUser);

        // then
        assertThat(updatedUser.getPassword()).isEqualTo("newEncodedPassword456");
        assertThat(updatedUser.getPassword()).isNotEqualTo(oldPassword);
    }

    @Test
    @DisplayName("사용자 저장 시 이메일 중복 제약조건 확인")
    void testUniqueEmailConstraint() {
        // given
        userRepository.save(testUser);

        User duplicateUser = User.builder()
                .email("test@example.com") // 동일한 이메일
                .password("anotherPassword")
                .nickname("Another User")
                .build();

        // when & then
        // DataIntegrityViolationException이 발생해야 함
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> {
                    userRepository.save(duplicateUser);
                    userRepository.flush();
                }
        );
    }

    @Test
    @DisplayName("여러 사용자 저장 및 조회")
    void testMultipleUsers() {
        // given
        User user1 = User.builder()
                .email("user1@example.com")
                .password("pass1")
                .nickname("User 1")
                .build();

        User user2 = User.builder()
                .email("user2@example.com")
                .password("pass2")
                .nickname("User 2")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // when
        Optional<User> foundUser1 = userRepository.findByEmail("user1@example.com");
        Optional<User> foundUser2 = userRepository.findByEmail("user2@example.com");

        // then
        assertThat(foundUser1).isPresent();
        assertThat(foundUser2).isPresent();
        assertThat(foundUser1.get().getNickname()).isEqualTo("User 1");
        assertThat(foundUser2.get().getNickname()).isEqualTo("User 2");
    }
}
