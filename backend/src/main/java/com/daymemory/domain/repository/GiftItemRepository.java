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

    // 키워드 검색 (최적화 - pg_trgm 인덱스 활용)
    // N+1 방지: User와 Event를 fetch join
    // 검색 대상: 이름, 설명, 메모
    // 정렬: 유사도 높은 순
    @Query(value = "SELECT DISTINCT g.* FROM gift_items g " +
           "LEFT JOIN users u ON g.user_id = u.id " +
           "LEFT JOIN events e ON g.event_id = e.id " +
           "WHERE g.user_id = :userId " +
           "AND (g.name ILIKE '%' || :keyword || '%' " +
           "     OR g.description ILIKE '%' || :keyword || '%' " +
           "     OR g.memo ILIKE '%' || :keyword || '%') " +
           "ORDER BY " +
           "  GREATEST(" +
           "    SIMILARITY(g.name, :keyword), " +
           "    COALESCE(SIMILARITY(g.description, :keyword), 0), " +
           "    COALESCE(SIMILARITY(g.memo, :keyword), 0)" +
           "  ) DESC",
           nativeQuery = true)
    List<GiftItem> searchByKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword);

    // 유사도 기반 검색 (임계값 적용 - 관련성 높은 것만)
    // 유사도 0.3 이상인 결과만 반환 (오타 허용)
    @Query(value = "SELECT DISTINCT g.* FROM gift_items g " +
           "WHERE g.user_id = :userId " +
           "AND (" +
           "  SIMILARITY(g.name, :keyword) > 0.3 " +
           "  OR SIMILARITY(COALESCE(g.description, ''), :keyword) > 0.3 " +
           "  OR SIMILARITY(COALESCE(g.memo, ''), :keyword) > 0.3" +
           ") " +
           "ORDER BY " +
           "  GREATEST(" +
           "    SIMILARITY(g.name, :keyword), " +
           "    SIMILARITY(COALESCE(g.description, ''), :keyword), " +
           "    SIMILARITY(COALESCE(g.memo, ''), :keyword)" +
           "  ) DESC " +
           "LIMIT 20",
           nativeQuery = true)
    List<GiftItem> searchBySimilarity(
            @Param("userId") Long userId,
            @Param("keyword") String keyword);
}
