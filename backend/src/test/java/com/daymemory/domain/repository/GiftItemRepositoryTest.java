package com.daymemory.domain.repository;

import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.GiftItem;
import com.daymemory.domain.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("GiftItemRepository 테스트")
class GiftItemRepositoryTest {

    @Autowired
    private GiftItemRepository giftItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EntityManager entityManager;

    private User testUser;
    private Event testEvent;
    private GiftItem testGift;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .emailVerified(true)
                .build();
        testUser = userRepository.save(testUser);

        // 테스트용 이벤트 생성
        testEvent = Event.builder()
                .user(testUser)
                .title("Birthday")
                .eventDate(LocalDate.now().plusDays(30))
                .eventType(Event.EventType.BIRTHDAY)
                .isActive(true)
                .build();
        testEvent = eventRepository.save(testEvent);

        // 테스트용 선물 생성
        testGift = GiftItem.builder()
                .user(testUser)
                .event(testEvent)
                .name("Test Gift")
                .description("Test Description")
                .price(50000)
                .url("https://example.com/gift")
                .category(GiftItem.GiftCategory.FLOWER)
                .isPurchased(false)
                .build();
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        giftItemRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자별 선물 조회")
    void testFindByUserId() {
        // given
        giftItemRepository.save(testGift);

        GiftItem anotherGift = GiftItem.builder()
                .user(testUser)
                .event(testEvent)
                .name("Another Gift")
                .category(GiftItem.GiftCategory.JEWELRY)
                .isPurchased(false)
                .build();
        giftItemRepository.save(anotherGift);

        entityManager.flush();
        entityManager.clear();

        // when
        List<GiftItem> gifts = giftItemRepository.findByUserId(testUser.getId());

        // then
        assertThat(gifts).hasSize(2);
        assertThat(gifts).extracting("name")
                .containsExactlyInAnyOrder("Test Gift", "Another Gift");

        // N+1 문제 방지 확인 - User와 Event가 이미 fetch join 되어 있어야 함
        assertThat(gifts.get(0).getUser()).isNotNull();
        assertThat(gifts.get(0).getUser().getName()).isEqualTo("Test User");
        assertThat(gifts.get(0).getEvent()).isNotNull();
        assertThat(gifts.get(0).getEvent().getTitle()).isEqualTo("Birthday");
    }

    @Test
    @DisplayName("이벤트별 선물 조회")
    void testFindByEventId() {
        // given
        giftItemRepository.save(testGift);

        Event anotherEvent = Event.builder()
                .user(testUser)
                .title("Anniversary")
                .eventDate(LocalDate.now().plusDays(60))
                .eventType(Event.EventType.ANNIVERSARY_100)
                .isActive(true)
                .build();
        anotherEvent = eventRepository.save(anotherEvent);

        GiftItem giftForAnotherEvent = GiftItem.builder()
                .user(testUser)
                .event(anotherEvent)
                .name("Anniversary Gift")
                .category(GiftItem.GiftCategory.JEWELRY)
                .isPurchased(false)
                .build();
        giftItemRepository.save(giftForAnotherEvent);

        entityManager.flush();
        entityManager.clear();

        // when
        List<GiftItem> gifts = giftItemRepository.findByEventId(testEvent.getId());

        // then
        assertThat(gifts).hasSize(1);
        assertThat(gifts.get(0).getName()).isEqualTo("Test Gift");
        assertThat(gifts.get(0).getEvent().getTitle()).isEqualTo("Birthday");
    }

    @Test
    @DisplayName("사용자와 이벤트로 선물 조회")
    void testFindByUserIdAndEventId() {
        // given
        giftItemRepository.save(testGift);

        entityManager.flush();
        entityManager.clear();

        // when
        List<GiftItem> gifts = giftItemRepository.findByUserIdAndEventId(
                testUser.getId(), testEvent.getId());

        // then
        assertThat(gifts).hasSize(1);
        assertThat(gifts.get(0).getName()).isEqualTo("Test Gift");
        assertThat(gifts.get(0).getUser().getId()).isEqualTo(testUser.getId());
        assertThat(gifts.get(0).getEvent().getId()).isEqualTo(testEvent.getId());
    }

    @Test
    @DisplayName("미구매 선물 조회")
    void testFindByUserIdAndIsPurchasedFalse() {
        // given
        giftItemRepository.save(testGift);

        GiftItem purchasedGift = GiftItem.builder()
                .user(testUser)
                .event(testEvent)
                .name("Purchased Gift")
                .category(GiftItem.GiftCategory.COSMETICS)
                .isPurchased(true)
                .build();
        giftItemRepository.save(purchasedGift);

        entityManager.flush();
        entityManager.clear();

        // when
        List<GiftItem> unpurchasedGifts = giftItemRepository.findByUserIdAndIsPurchasedFalse(
                testUser.getId());

        // then
        assertThat(unpurchasedGifts).hasSize(1);
        assertThat(unpurchasedGifts.get(0).getName()).isEqualTo("Test Gift");
        assertThat(unpurchasedGifts.get(0).getIsPurchased()).isFalse();
    }

    @Test
    @DisplayName("특정 ID의 GiftItem을 User, Event와 함께 조회 - Fetch Join 동작 확인")
    void testFindByIdWithUserAndEvent() {
        // given
        GiftItem savedGift = giftItemRepository.save(testGift);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<GiftItem> foundGift = giftItemRepository.findByIdWithUserAndEvent(savedGift.getId());

        // then
        assertThat(foundGift).isPresent();

        // N+1 문제 방지 확인 - User와 Event가 이미 fetch join 되어 있어야 함
        GiftItem gift = foundGift.get();
        assertThat(gift.getUser()).isNotNull();
        assertThat(gift.getUser().getName()).isEqualTo("Test User");
        assertThat(gift.getEvent()).isNotNull();
        assertThat(gift.getEvent().getTitle()).isEqualTo("Birthday");
    }

    @Test
    @DisplayName("카테고리별 선물 조회")
    void testFindByUserIdAndCategory() {
        // given
        giftItemRepository.save(testGift);

        GiftItem jewelryGift = GiftItem.builder()
                .user(testUser)
                .event(testEvent)
                .name("Jewelry Gift")
                .category(GiftItem.GiftCategory.JEWELRY)
                .isPurchased(false)
                .build();
        giftItemRepository.save(jewelryGift);

        GiftItem cosmeticsGift = GiftItem.builder()
                .user(testUser)
                .event(testEvent)
                .name("Cosmetics Gift")
                .category(GiftItem.GiftCategory.COSMETICS)
                .isPurchased(false)
                .build();
        giftItemRepository.save(cosmeticsGift);

        entityManager.flush();
        entityManager.clear();

        // when
        List<GiftItem> flowerGifts = giftItemRepository.findByUserIdAndCategory(
                testUser.getId(), GiftItem.GiftCategory.FLOWER);

        // then
        assertThat(flowerGifts).hasSize(1);
        assertThat(flowerGifts.get(0).getName()).isEqualTo("Test Gift");
        assertThat(flowerGifts.get(0).getCategory()).isEqualTo(GiftItem.GiftCategory.FLOWER);
    }

    @Test
    @DisplayName("키워드로 선물 검색 - 이름 검색")
    void testSearchByKeyword_Name() {
        // given
        giftItemRepository.save(testGift);

        GiftItem flowerBouquet = GiftItem.builder()
                .user(testUser)
                .event(testEvent)
                .name("Beautiful Flower Bouquet")
                .description("Roses and tulips")
                .category(GiftItem.GiftCategory.FLOWER)
                .isPurchased(false)
                .build();
        giftItemRepository.save(flowerBouquet);

        entityManager.flush();
        entityManager.clear();

        // when
        List<GiftItem> gifts = giftItemRepository.searchByKeyword(testUser.getId(), "flower");

        // then
        assertThat(gifts).hasSize(1);
        assertThat(gifts.get(0).getName()).containsIgnoringCase("flower");
    }

    @Test
    @DisplayName("키워드로 선물 검색 - 설명 검색")
    void testSearchByKeyword_Description() {
        // given
        GiftItem gift = GiftItem.builder()
                .user(testUser)
                .event(testEvent)
                .name("Perfume")
                .description("Elegant fragrance with floral notes")
                .category(GiftItem.GiftCategory.COSMETICS)
                .isPurchased(false)
                .build();
        giftItemRepository.save(gift);

        entityManager.flush();
        entityManager.clear();

        // when
        List<GiftItem> gifts = giftItemRepository.searchByKeyword(testUser.getId(), "floral");

        // then
        assertThat(gifts).hasSize(1);
        assertThat(gifts.get(0).getDescription()).containsIgnoringCase("floral");
    }

    @Test
    @DisplayName("키워드 검색 - 대소문자 구분 없이 검색")
    void testSearchByKeyword_CaseInsensitive() {
        // given
        giftItemRepository.save(testGift);

        entityManager.flush();
        entityManager.clear();

        // when
        List<GiftItem> giftsLowerCase = giftItemRepository.searchByKeyword(testUser.getId(), "test");
        List<GiftItem> giftsUpperCase = giftItemRepository.searchByKeyword(testUser.getId(), "TEST");
        List<GiftItem> giftsMixedCase = giftItemRepository.searchByKeyword(testUser.getId(), "TeSt");

        // then
        assertThat(giftsLowerCase).hasSize(1);
        assertThat(giftsUpperCase).hasSize(1);
        assertThat(giftsMixedCase).hasSize(1);
    }

    @Test
    @DisplayName("선물 정보 업데이트 테스트")
    void testUpdateGift() {
        // given
        GiftItem savedGift = giftItemRepository.save(testGift);

        // when
        savedGift.update(
                "Updated Gift",
                "Updated Description",
                100000,
                "https://example.com/updated",
                GiftItem.GiftCategory.JEWELRY
        );
        GiftItem updatedGift = giftItemRepository.save(savedGift);

        // then
        assertThat(updatedGift.getName()).isEqualTo("Updated Gift");
        assertThat(updatedGift.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedGift.getPrice()).isEqualTo(100000);
        assertThat(updatedGift.getUrl()).isEqualTo("https://example.com/updated");
        assertThat(updatedGift.getCategory()).isEqualTo(GiftItem.GiftCategory.JEWELRY);
    }

    @Test
    @DisplayName("선물 구매 상태 변경 테스트")
    void testMarkAsPurchased() {
        // given
        GiftItem savedGift = giftItemRepository.save(testGift);
        assertThat(savedGift.getIsPurchased()).isFalse();

        // when
        savedGift.markAsPurchased();
        GiftItem purchasedGift = giftItemRepository.save(savedGift);

        // then
        assertThat(purchasedGift.getIsPurchased()).isTrue();

        // when - 구매 상태 취소
        purchasedGift.markAsNotPurchased();
        GiftItem unpurchasedGift = giftItemRepository.save(purchasedGift);

        // then
        assertThat(unpurchasedGift.getIsPurchased()).isFalse();
    }

    @Test
    @DisplayName("이미지 URL 업데이트 테스트")
    void testUpdateImageUrl() {
        // given
        GiftItem savedGift = giftItemRepository.save(testGift);
        assertThat(savedGift.getImageUrl()).isNull();

        // when
        savedGift.updateImageUrl("https://example.com/image.jpg");
        GiftItem updatedGift = giftItemRepository.save(savedGift);

        // then
        assertThat(updatedGift.getImageUrl()).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    @DisplayName("이벤트 없이 선물 생성 가능")
    void testGiftWithoutEvent() {
        // given
        GiftItem giftWithoutEvent = GiftItem.builder()
                .user(testUser)
                .name("Wishlist Item")
                .description("General wishlist item")
                .category(GiftItem.GiftCategory.OTHER)
                .isPurchased(false)
                .build();

        // when
        GiftItem savedGift = giftItemRepository.save(giftWithoutEvent);

        // then
        assertThat(savedGift.getId()).isNotNull();
        assertThat(savedGift.getEvent()).isNull();
        assertThat(savedGift.getUser()).isNotNull();
    }

    @Test
    @DisplayName("가격 없이 선물 생성 가능")
    void testGiftWithoutPrice() {
        // given
        GiftItem giftWithoutPrice = GiftItem.builder()
                .user(testUser)
                .event(testEvent)
                .name("Priceless Gift")
                .description("Special gift with no price")
                .category(GiftItem.GiftCategory.OTHER)
                .isPurchased(false)
                .build();

        // when
        GiftItem savedGift = giftItemRepository.save(giftWithoutPrice);

        // then
        assertThat(savedGift.getId()).isNotNull();
        assertThat(savedGift.getPrice()).isNull();
    }

    @Test
    @DisplayName("다양한 카테고리의 선물 생성 및 조회")
    void testDifferentCategories() {
        // given
        for (GiftItem.GiftCategory category : GiftItem.GiftCategory.values()) {
            GiftItem gift = GiftItem.builder()
                    .user(testUser)
                    .event(testEvent)
                    .name(category.name() + " Gift")
                    .category(category)
                    .isPurchased(false)
                    .build();
            giftItemRepository.save(gift);
        }

        entityManager.flush();
        entityManager.clear();

        // when
        List<GiftItem> allGifts = giftItemRepository.findByUserId(testUser.getId());

        // then
        assertThat(allGifts).hasSize(GiftItem.GiftCategory.values().length);
        assertThat(allGifts).extracting("category")
                .containsExactlyInAnyOrder(GiftItem.GiftCategory.values());
    }
}
