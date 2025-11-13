package com.daymemory.domain.repository;

import com.daymemory.domain.entity.User;
import com.daymemory.domain.entity.VerificationToken;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("VerificationTokenRepository 테스트")
class VerificationTokenRepositoryTest {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("Test User")
                .emailVerified(false)
                .build();
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        verificationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("토큰으로 검증 토큰 조회 성공")
    void testFindByToken_Success() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(verificationToken);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<VerificationToken> foundToken = verificationTokenRepository.findByToken(token);

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo(token);
        assertThat(foundToken.get().getType()).isEqualTo(VerificationToken.TokenType.EMAIL_VERIFICATION);
        assertThat(foundToken.get().getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 토큰 조회 시 빈 Optional 반환")
    void testFindByToken_NotFound() {
        // when
        Optional<VerificationToken> foundToken = verificationTokenRepository.findByToken(
                "nonexistent-token");

        // then
        assertThat(foundToken).isEmpty();
    }

    @Test
    @DisplayName("사용자 ID와 타입으로 미사용 토큰 조회 성공")
    void testFindByUserIdAndTypeAndIsUsedFalse_Success() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(verificationToken);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<VerificationToken> foundToken = verificationTokenRepository
                .findByUserIdAndTypeAndIsUsedFalse(
                        testUser.getId(),
                        VerificationToken.TokenType.EMAIL_VERIFICATION
                );

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo(token);
        assertThat(foundToken.get().getIsUsed()).isFalse();
        assertThat(foundToken.get().getType()).isEqualTo(VerificationToken.TokenType.EMAIL_VERIFICATION);
    }

    @Test
    @DisplayName("사용된 토큰은 미사용 토큰 조회에 포함되지 않음")
    void testFindByUserIdAndTypeAndIsUsedFalse_UsedTokenNotIncluded() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(true)
                .usedAt(LocalDateTime.now())
                .build();
        verificationTokenRepository.save(verificationToken);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<VerificationToken> foundToken = verificationTokenRepository
                .findByUserIdAndTypeAndIsUsedFalse(
                        testUser.getId(),
                        VerificationToken.TokenType.EMAIL_VERIFICATION
                );

        // then
        assertThat(foundToken).isEmpty();
    }

    @Test
    @DisplayName("다른 타입의 토큰은 조회되지 않음")
    void testFindByUserIdAndTypeAndIsUsedFalse_DifferentTypeNotIncluded() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken emailToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(emailToken);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<VerificationToken> foundToken = verificationTokenRepository
                .findByUserIdAndTypeAndIsUsedFalse(
                        testUser.getId(),
                        VerificationToken.TokenType.PASSWORD_RESET
                );

        // then
        assertThat(foundToken).isEmpty();
    }

    @Test
    @DisplayName("토큰 사용 처리 테스트")
    void testMarkAsUsed() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();
        VerificationToken savedToken = verificationTokenRepository.save(verificationToken);
        assertThat(savedToken.getIsUsed()).isFalse();
        assertThat(savedToken.getUsedAt()).isNull();

        // when
        savedToken.markAsUsed();
        VerificationToken usedToken = verificationTokenRepository.save(savedToken);

        // then
        assertThat(usedToken.getIsUsed()).isTrue();
        assertThat(usedToken.getUsedAt()).isNotNull();
        assertThat(usedToken.getUsedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("토큰 만료 여부 확인 - 만료되지 않음")
    void testIsExpired_NotExpired() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();
        VerificationToken savedToken = verificationTokenRepository.save(verificationToken);

        // when
        boolean isExpired = savedToken.isExpired();

        // then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("토큰 만료 여부 확인 - 만료됨")
    void testIsExpired_Expired() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .isUsed(false)
                .build();
        VerificationToken savedToken = verificationTokenRepository.save(verificationToken);

        // when
        boolean isExpired = savedToken.isExpired();

        // then
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("이메일 인증 토큰 생성 및 조회")
    void testEmailVerificationToken() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(verificationToken);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<VerificationToken> foundToken = verificationTokenRepository
                .findByUserIdAndTypeAndIsUsedFalse(
                        testUser.getId(),
                        VerificationToken.TokenType.EMAIL_VERIFICATION
                );

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getType()).isEqualTo(VerificationToken.TokenType.EMAIL_VERIFICATION);
    }

    @Test
    @DisplayName("비밀번호 재설정 토큰 생성 및 조회")
    void testPasswordResetToken() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.PASSWORD_RESET)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(verificationToken);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<VerificationToken> foundToken = verificationTokenRepository
                .findByUserIdAndTypeAndIsUsedFalse(
                        testUser.getId(),
                        VerificationToken.TokenType.PASSWORD_RESET
                );

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getType()).isEqualTo(VerificationToken.TokenType.PASSWORD_RESET);
    }

    @Test
    @DisplayName("한 사용자가 여러 타입의 토큰을 가질 수 있음")
    void testMultipleTokenTypesPerUser() {
        // given
        String emailToken = UUID.randomUUID().toString();
        VerificationToken emailVerificationToken = VerificationToken.builder()
                .token(emailToken)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(emailVerificationToken);

        String passwordToken = UUID.randomUUID().toString();
        VerificationToken passwordResetToken = VerificationToken.builder()
                .token(passwordToken)
                .user(testUser)
                .type(VerificationToken.TokenType.PASSWORD_RESET)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(passwordResetToken);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<VerificationToken> foundEmailToken = verificationTokenRepository
                .findByUserIdAndTypeAndIsUsedFalse(
                        testUser.getId(),
                        VerificationToken.TokenType.EMAIL_VERIFICATION
                );

        Optional<VerificationToken> foundPasswordToken = verificationTokenRepository
                .findByUserIdAndTypeAndIsUsedFalse(
                        testUser.getId(),
                        VerificationToken.TokenType.PASSWORD_RESET
                );

        // then
        assertThat(foundEmailToken).isPresent();
        assertThat(foundPasswordToken).isPresent();
        assertThat(foundEmailToken.get().getToken()).isEqualTo(emailToken);
        assertThat(foundPasswordToken.get().getToken()).isEqualTo(passwordToken);
    }

    @Test
    @DisplayName("만료되고 사용된 토큰 삭제")
    void testDeleteByExpiresAtBeforeAndIsUsedTrue() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // 만료되고 사용된 토큰
        String expiredUsedToken = UUID.randomUUID().toString();
        VerificationToken expiredUsed = VerificationToken.builder()
                .token(expiredUsedToken)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(now.minusDays(2))
                .isUsed(true)
                .usedAt(now.minusDays(3))
                .build();
        verificationTokenRepository.save(expiredUsed);

        // 만료되었지만 사용되지 않은 토큰 (삭제되지 않아야 함)
        String expiredUnusedToken = UUID.randomUUID().toString();
        VerificationToken expiredUnused = VerificationToken.builder()
                .token(expiredUnusedToken)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(now.minusDays(2))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(expiredUnused);

        // 만료되지 않고 사용된 토큰 (삭제되지 않아야 함)
        String validUsedToken = UUID.randomUUID().toString();
        VerificationToken validUsed = VerificationToken.builder()
                .token(validUsedToken)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(now.plusDays(1))
                .isUsed(true)
                .usedAt(now.minusHours(1))
                .build();
        verificationTokenRepository.save(validUsed);

        entityManager.flush();
        entityManager.clear();

        // when
        verificationTokenRepository.deleteByExpiresAtBeforeAndIsUsedTrue(now.minusDays(1));
        entityManager.flush();
        entityManager.clear();

        // then
        Optional<VerificationToken> deletedToken = verificationTokenRepository.findByToken(expiredUsedToken);
        Optional<VerificationToken> expiredUnusedStillExists = verificationTokenRepository.findByToken(expiredUnusedToken);
        Optional<VerificationToken> validUsedStillExists = verificationTokenRepository.findByToken(validUsedToken);

        assertThat(deletedToken).isEmpty();
        assertThat(expiredUnusedStillExists).isPresent();
        assertThat(validUsedStillExists).isPresent();
    }

    @Test
    @DisplayName("토큰 고유성 제약조건 확인")
    void testUniqueTokenConstraint() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken token1 = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(token1);

        VerificationToken token2 = VerificationToken.builder()
                .token(token) // 동일한 토큰
                .user(testUser)
                .type(VerificationToken.TokenType.PASSWORD_RESET)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .isUsed(false)
                .build();

        // when & then
        // DataIntegrityViolationException이 발생해야 함
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> {
                    verificationTokenRepository.save(token2);
                    verificationTokenRepository.flush();
                }
        );
    }

    @Test
    @DisplayName("토큰 생성 시 기본값은 미사용 상태")
    void testDefaultIsUsedFalse() {
        // given
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        // when
        VerificationToken savedToken = verificationTokenRepository.save(verificationToken);

        // then
        assertThat(savedToken.getIsUsed()).isFalse();
        assertThat(savedToken.getUsedAt()).isNull();
    }

    @Test
    @DisplayName("서로 다른 사용자의 토큰은 독립적으로 관리됨")
    void testTokensAreIndependentPerUser() {
        // given
        User anotherUser = User.builder()
                .email("another@example.com")
                .password("password")
                .nickname("Another User")
                .emailVerified(false)
                .build();
        anotherUser = userRepository.save(anotherUser);

        String token1 = UUID.randomUUID().toString();
        VerificationToken testUserToken = VerificationToken.builder()
                .token(token1)
                .user(testUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(testUserToken);

        String token2 = UUID.randomUUID().toString();
        VerificationToken anotherUserToken = VerificationToken.builder()
                .token(token2)
                .user(anotherUser)
                .type(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();
        verificationTokenRepository.save(anotherUserToken);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<VerificationToken> testUserFoundToken = verificationTokenRepository
                .findByUserIdAndTypeAndIsUsedFalse(
                        testUser.getId(),
                        VerificationToken.TokenType.EMAIL_VERIFICATION
                );

        Optional<VerificationToken> anotherUserFoundToken = verificationTokenRepository
                .findByUserIdAndTypeAndIsUsedFalse(
                        anotherUser.getId(),
                        VerificationToken.TokenType.EMAIL_VERIFICATION
                );

        // then
        assertThat(testUserFoundToken).isPresent();
        assertThat(anotherUserFoundToken).isPresent();
        assertThat(testUserFoundToken.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(anotherUserFoundToken.get().getUser().getId()).isEqualTo(anotherUser.getId());
    }
}
