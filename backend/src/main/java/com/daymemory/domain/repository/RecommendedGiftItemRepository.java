package com.daymemory.domain.repository;

import com.daymemory.domain.entity.RecommendedGiftItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedGiftItemRepository extends JpaRepository<RecommendedGiftItem, Long> {

    @Query("SELECT DISTINCT r FROM RecommendedGiftItem r " +
           "LEFT JOIN FETCH r.recommendation " +
           "LEFT JOIN FETCH r.savedGift " +
           "WHERE r.recommendation.id = :recommendationId")
    List<RecommendedGiftItem> findByRecommendationId(@Param("recommendationId") Long recommendationId);
}
