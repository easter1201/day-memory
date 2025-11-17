package com.daymemory.domain.repository;

import com.daymemory.domain.entity.AIRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, Long> {

    // User와 Event를 함께 조회 (N+1 방지)
    @Query("SELECT DISTINCT r FROM AIRecommendation r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.event " +
           "WHERE r.user.id = :userId " +
           "ORDER BY r.createdAt DESC")
    List<AIRecommendation> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // ID로 조회 시 User와 Event를 함께 조회
    @Query("SELECT DISTINCT r FROM AIRecommendation r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.event " +
           "WHERE r.id = :recommendationId")
    Optional<AIRecommendation> findByIdWithUserAndEvent(@Param("recommendationId") Long recommendationId);
}
