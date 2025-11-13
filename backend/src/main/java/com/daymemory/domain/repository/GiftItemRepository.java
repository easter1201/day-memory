package com.daymemory.domain.repository;

import com.daymemory.domain.entity.GiftItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiftItemRepository extends JpaRepository<GiftItem, Long> {

    // N+1 문제 방지: User와 Event를 fetch join
    @Query("SELECT DISTINCT g FROM GiftItem g " +
           "LEFT JOIN FETCH g.user " +
           "LEFT JOIN FETCH g.event " +
           "WHERE g.user.id = :userId")
    List<GiftItem> findByUserId(@Param("userId") Long userId);

    // N+1 문제 방지: User와 Event를 fetch join
    @Query("SELECT DISTINCT g FROM GiftItem g " +
           "LEFT JOIN FETCH g.user " +
           "LEFT JOIN FETCH g.event " +
           "WHERE g.event.id = :eventId")
    List<GiftItem> findByEventId(@Param("eventId") Long eventId);

    // N+1 문제 방지: User와 Event를 fetch join
    @Query("SELECT DISTINCT g FROM GiftItem g " +
           "LEFT JOIN FETCH g.user " +
           "LEFT JOIN FETCH g.event " +
           "WHERE g.user.id = :userId AND g.event.id = :eventId")
    List<GiftItem> findByUserIdAndEventId(
            @Param("userId") Long userId,
            @Param("eventId") Long eventId);

    // N+1 문제 방지: User와 Event를 fetch join (구매 완료)
    @Query("SELECT DISTINCT g FROM GiftItem g " +
           "LEFT JOIN FETCH g.user " +
           "LEFT JOIN FETCH g.event " +
           "WHERE g.user.id = :userId AND g.isPurchased = true")
    List<GiftItem> findByUserIdAndIsPurchasedTrue(@Param("userId") Long userId);

    // N+1 문제 방지: User와 Event를 fetch join (미구매)
    @Query("SELECT DISTINCT g FROM GiftItem g " +
           "LEFT JOIN FETCH g.user " +
           "LEFT JOIN FETCH g.event " +
           "WHERE g.user.id = :userId AND g.isPurchased = false")
    List<GiftItem> findByUserIdAndIsPurchasedFalse(@Param("userId") Long userId);

    // 특정 ID의 GiftItem을 User, Event와 함께 조회 (N+1 방지)
    @Query("SELECT DISTINCT g FROM GiftItem g " +
           "LEFT JOIN FETCH g.user " +
           "LEFT JOIN FETCH g.event " +
           "WHERE g.id = :giftId")
    Optional<GiftItem> findByIdWithUserAndEvent(@Param("giftId") Long giftId);

    // 카테고리별 선물 조회 (N+1 방지)
    @Query("SELECT DISTINCT g FROM GiftItem g " +
           "LEFT JOIN FETCH g.user " +
           "LEFT JOIN FETCH g.event " +
           "WHERE g.user.id = :userId AND g.category = :category")
    List<GiftItem> findByUserIdAndCategory(
            @Param("userId") Long userId,
            @Param("category") GiftItem.GiftCategory category);

    // 구매 상태와 카테고리로 필터링 (N+1 방지)
    @Query("SELECT DISTINCT g FROM GiftItem g " +
           "LEFT JOIN FETCH g.user " +
           "LEFT JOIN FETCH g.event " +
           "WHERE g.user.id = :userId AND g.isPurchased = :isPurchased AND g.category = :category")
    List<GiftItem> findByUserIdAndIsPurchasedAndCategory(
            @Param("userId") Long userId,
            @Param("isPurchased") Boolean isPurchased,
            @Param("category") GiftItem.GiftCategory category);

    // 키워드 검색 (N+1 방지) - 선물 이름, 설명에서 검색
    @Query("SELECT DISTINCT g FROM GiftItem g " +
           "LEFT JOIN FETCH g.user " +
           "LEFT JOIN FETCH g.event " +
           "WHERE g.user.id = :userId " +
           "AND (LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<GiftItem> searchByKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword);
}
