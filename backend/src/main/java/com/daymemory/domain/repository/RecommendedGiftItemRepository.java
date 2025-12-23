package com.daymemory.domain.repository;

import com.daymemory.domain.entity.RecommendedGiftItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedGiftItemRepository extends JpaRepository<RecommendedGiftItem, Long> {

    // Recommendation ID로 조회 (N+1 방지)
    @Query("SELECT DISTINCT r FROM RecommendedGiftItem r " +
           "LEFT JOIN FETCH r.recommendation " +
           "LEFT JOIN FETCH r.savedGift " +
           "WHERE r.recommendation.id = :recommendationId")
    List<RecommendedGiftItem> findByRecommendationId(@Param("recommendationId") Long recommendationId);

    // SavedGift ID로 조회 (deleteGiftItem 최적화)
    @Query("SELECT r FROM RecommendedGiftItem r " +
           "WHERE r.savedGift.id = :savedGiftId")
    List<RecommendedGiftItem> findBySavedGiftId(@Param("savedGiftId") Long savedGiftId);
}
