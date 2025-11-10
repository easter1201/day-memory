package com.daymemory.domain.repository;

import com.daymemory.domain.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUserIdAndTypeAndIsUsedFalse(
            Long userId, VerificationToken.TokenType type);

    void deleteByExpiresAtBeforeAndIsUsedTrue(LocalDateTime dateTime);
}
