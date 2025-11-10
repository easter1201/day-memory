# 시스템 아키텍처 개요 (04_architecture.md)

## 📌 개요

Day Memory 프로젝트의 백엔드 시스템 아키텍처를 설명합니다.
기술 스택, 계층 구조, 인증 흐름, 스케줄링 처리, 외부 연동 등 전체 구조를 요약합니다.

---

## 1. 기술 스택

### Backend Framework
- **Java**: 17 (LTS)
- **Spring Boot**: 3.x
- **Spring Data JPA**: Hibernate 기반 ORM
- **Build Tool**: Maven 3.8+

### Database
- **Primary DB**: PostgreSQL 14+
- **Connection Pool**: HikariCP (기본 포함)

### Security & Authentication
- **Spring Security**: 6.x
- **JWT**: JSON Web Token 기반 인증
- **Password Encryption**: BCrypt

### External Integration
- **Email**: JavaMailSender (SMTP - Gmail 또는 AWS SES)
- **AI**: OpenAI API / Anthropic Claude API (선물 추천)
- **Scheduler**: Spring `@Scheduled` (매일 오전 9시 리마인더 발송)

### Documentation & Testing
- **API Documentation**: Springdoc OpenAPI (Swagger UI)
- **Testing**: JUnit 5, Mockito, AssertJ
- **Integration Testing**: @SpringBootTest, MockMvc

### Development Tools
- **IDE**: IntelliJ IDEA Ultimate
- **API Testing**: Postman
- **Database Management**: DBeaver, pgAdmin
- **Version Control**: Git, GitHub

---

## 2. 계층화 아키텍처 (Layered Architecture)

```
┌──────────────────────────────────────────────────┐
│            Presentation Layer                    │
│  - REST Controller (JSON API)                    │
│  - DTO (Request/Response)                        │
│  - GlobalExceptionHandler                        │
│  - CORS Configuration                            │
└────────────────┬─────────────────────────────────┘
                 │
┌────────────────┴─────────────────────────────────┐
│            Security Layer                        │
│  - JwtAuthenticationFilter                       │
│  - JwtTokenProvider                              │
│  - CustomUserDetailsService                      │
└────────────────┬─────────────────────────────────┘
                 │
┌────────────────┴─────────────────────────────────┐
│         Business Logic Layer                     │
│  - Service (비즈니스 로직)                         │
│  - Domain Model (Entity)                         │
│  - DTO Conversion (Entity ↔ DTO)                │
│  - Scheduler (@Scheduled)                        │
└────────────────┬─────────────────────────────────┘
                 │
┌────────────────┴─────────────────────────────────┐
│         Data Access Layer                        │
│  - Repository (Spring Data JPA)                  │
│  - Custom Query (fetch join)                     │
│  - N+1 문제 해결 (LEFT JOIN FETCH)                │
└────────────────┬─────────────────────────────────┘
                 │
┌────────────────┴─────────────────────────────────┐
│         Database (PostgreSQL)                    │
│  - users, events, event_reminders                │
│  - gift_items, reminder_logs                     │
└──────────────────────────────────────────────────┘
```

---

## 3. 폴더 구조

```
src/main/java/com/daymemory/
├── DayMemoryApplication.java              # Main 클래스
│
├── config/                                 # 설정 클래스
│   ├── JpaConfig.java                     # JPA Auditing 활성화
│   ├── CorsConfig.java                    # CORS 설정
│   ├── SecurityConfig.java                # Spring Security 설정
│   └── SwaggerConfig.java                 # Swagger 문서화 설정
│
├── controller/                             # REST API 컨트롤러
│   ├── UserController.java                # 사용자 인증 API
│   ├── EventController.java               # 이벤트 CRUD API
│   ├── GiftItemController.java            # 선물 CRUD API
│   └── DashboardController.java           # 대시보드 요약 API
│
├── service/                                # 비즈니스 로직
│   ├── UserService.java                   # 사용자 관리
│   ├── EventService.java                  # 이벤트 관리
│   ├── GiftItemService.java               # 선물 관리
│   ├── ReminderService.java               # 리마인더 스케줄러
│   ├── EmailService.java                  # 이메일 발송
│   └── AIRecommendationService.java       # AI 선물 추천
│
├── domain/
│   ├── entity/                             # JPA 엔티티
│   │   ├── BaseEntity.java                # 공통 필드 (createdAt, updatedAt)
│   │   ├── User.java                      # 사용자
│   │   ├── Event.java                     # 이벤트/기념일
│   │   ├── EventReminder.java             # 리마인더 설정 (유연한 일수)
│   │   ├── GiftItem.java                  # 선물 아이템
│   │   └── ReminderLog.java               # 리마인더 발송 로그
│   │
│   ├── repository/                         # Spring Data JPA Repository
│   │   ├── UserRepository.java
│   │   ├── EventRepository.java
│   │   ├── EventReminderRepository.java
│   │   ├── GiftItemRepository.java
│   │   └── ReminderLogRepository.java
│   │
│   └── dto/                                # Data Transfer Object
│       ├── UserDto.java                   # Request/Response DTO
│       ├── EventDto.java
│       └── GiftItemDto.java
│
├── security/                               # 보안 관련 클래스
│   ├── JwtTokenProvider.java              # JWT 생성/검증
│   ├── JwtAuthenticationFilter.java       # JWT 필터
│   └── CustomUserDetailsService.java      # 사용자 인증 정보 로드
│
├── exception/                              # 예외 처리
│   ├── CustomException.java               # 커스텀 예외 클래스
│   ├── ErrorCode.java                     # 에러 코드 enum
│   └── GlobalExceptionHandler.java        # 전역 예외 핸들러
│
└── util/                                   # 유틸리티
    ├── ResponseWrapper.java               # 공통 응답 포맷
    └── EmailTemplateUtil.java             # 이메일 템플릿 생성

src/main/resources/
├── application.properties                  # 기본 설정
├── application-dev.properties              # 개발 환경
├── application-prod.properties             # 운영 환경
└── templates/                              # 이메일 템플릿
    └── reminder-email.html                # 리마인더 이메일 HTML

src/test/java/com/daymemory/
├── repository/                             # Repository 단위 테스트
├── service/                                # Service 단위 테스트
└── controller/                             # Controller 통합 테스트
```

---

## 4. 인증 흐름 (JWT 기반)

### 회원가입 및 로그인

```
Client → POST /api/users/signup (username, email, password)
         ↓
     UserController
         ↓
     UserService (비밀번호 BCrypt 암호화)
         ↓
     UserRepository.save()
         ↓
     Database (users 테이블)

─────────────────────────────────────────────────────

Client → POST /api/users/login (email, password)
         ↓
     UserController
         ↓
     UserService (비밀번호 검증)
         ↓
     JwtTokenProvider.generateAccessToken()
     JwtTokenProvider.generateRefreshToken()
         ↓
     Response: { accessToken, refreshToken, user: {...} }
```

### 인증이 필요한 API 요청

```
Client → GET /api/events (Header: Authorization: Bearer {accessToken})
         ↓
     JwtAuthenticationFilter
         ↓
     JwtTokenProvider.validateToken() (토큰 검증)
         ↓
     JwtTokenProvider.getUserIdFromToken()
         ↓
     SecurityContext에 인증 정보 설정
         ↓
     EventController → EventService → EventRepository
         ↓
     Response: 이벤트 목록
```

### 토큰 재발급

```
Client → POST /api/users/refresh (refreshToken)
         ↓
     UserController
         ↓
     JwtTokenProvider.validateRefreshToken()
         ↓
     JwtTokenProvider.generateAccessToken() (새 Access Token 발급)
         ↓
     Response: { accessToken }
```

---

## 5. 스케줄링 처리 (리마인더 발송)

### 매일 오전 9시 자동 실행

```
@Scheduled(cron = "0 0 9 * * ?")
         ↓
    ReminderService.sendDailyReminders()
         ↓
    EventRepository.findTrackingEventsBetweenDates(
        today + 1일,
        today + 365일
    ) // 향후 1년치 이벤트 조회
         ↓
    각 Event의 reminders 리스트 확인
         ↓
    활성화된 리마인더(isActive = true) 필터링
         ↓
    daysBeforeEvent 계산:
    daysUntil = eventDate - today
    리마인더 발송 대상: daysUntil == reminder.daysBeforeEvent
         ↓
    중복 발송 방지:
    ReminderLogRepository에서 24시간 이내 동일 event_id + daysBeforeEvent 확인
         ↓
    EmailService.sendEmail(user.email, subject, body)
         ↓
    ReminderLog 저장 (event_id, days_before_event, sent_at)
```

### 즉시 발송 기능 (테스트용)

```
POST /api/events/{eventId}/send-reminder
         ↓
    EventController
         ↓
    ReminderService.sendImmediateReminder(eventId)
         ↓
    EmailService.sendEmail()
         ↓
    Response: { message: "리마인더가 즉시 발송되었습니다." }
```

---

## 6. 데이터 흐름 (요청/응답)

### 이벤트 생성 예시

```
Client (React)
    ↓
POST /api/events
Body: {
  "title": "발렌타인데이",
  "eventDate": "2025-02-14",
  "eventType": "VALENTINES_DAY",
  "isTracking": true,
  "reminderDays": [30, 14, 7, 1]
}
    ↓
EventController.createEvent(@RequestBody EventDto.Request)
    ↓
@Valid 검증 (title, eventDate 필수값 확인)
    ↓
EventService.createEvent(request, userId)
    ↓
Event 엔티티 생성 (Builder 패턴)
    ↓
reminderDays를 순회하며 EventReminder 엔티티 생성
event.addReminder(reminder) (양방향 연관관계 설정)
    ↓
EventRepository.save(event) (cascade로 reminders도 함께 저장)
    ↓
Database (events, event_reminders 테이블)
    ↓
EventDto.Response.from(event) (Entity → DTO 변환)
    ↓
Response: {
  "id": 1,
  "title": "발렌타인데이",
  "eventDate": "2025-02-14",
  "dDay": 35,
  "isTracking": true,
  "reminders": [
    { "id": 1, "daysBeforeEvent": 30, "isActive": true },
    { "id": 2, "daysBeforeEvent": 14, "isActive": true },
    { "id": 3, "daysBeforeEvent": 7, "isActive": true },
    { "id": 4, "daysBeforeEvent": 1, "isActive": true }
  ]
}
```

---

## 7. N+1 문제 해결 전략

### 문제 상황
```java
// N+1 발생 예시
List<Event> events = eventRepository.findByUserId(userId);
for (Event event : events) {
    event.getUser().getUsername(); // 추가 쿼리 발생
    event.getReminders().size();   // 추가 쿼리 발생
}
```

### 해결 방법: fetch join 사용
```java
@Query("SELECT DISTINCT e FROM Event e " +
       "LEFT JOIN FETCH e.user " +
       "LEFT JOIN FETCH e.reminders " +
       "WHERE e.user.id = :userId AND e.isActive = true")
List<Event> findByUserIdAndIsActiveTrue(@Param("userId") Long userId);
```

### 적용된 Repository
- EventRepository: user, reminders fetch join
- GiftItemRepository: user, event fetch join
- ReminderLogRepository: event fetch join

---

## 8. 외부 연동

### Email (JavaMailSender)
```
EmailService
    ↓
JavaMailSender.createMimeMessage()
    ↓
MimeMessageHelper (HTML 이메일 구성)
    ↓
templates/reminder-email.html 템플릿 사용
    ↓
SMTP Server (Gmail: smtp.gmail.com:587)
    ↓
사용자 이메일로 발송
```

### AI 선물 추천 (OpenAI / Claude API)
```
Client → POST /api/gifts/recommend
Body: {
  "eventId": 1,
  "budget": 50000,
  "preferences": ["실용적", "귀여운"]
}
    ↓
GiftItemController
    ↓
AIRecommendationService.recommendGifts(eventId, budget, preferences)
    ↓
Event 정보 조회 (eventType, recipient)
    ↓
프롬프트 구성:
"2025년 발렌타인데이를 위한 선물 추천.
예산: 50000원, 선호: 실용적, 귀여운"
    ↓
OpenAI API / Claude API 호출
    ↓
AI 응답 파싱 (선물명, 설명, 이유)
    ↓
사용자 저장 선물 우선 표시
    ↓
Response: {
  "recommendations": [
    { "name": "향수", "reason": "실용적이고 로맨틱함" },
    ...
  ]
}
```

---

## 9. 캐시 및 성능 최적화

### 현재 구현
- **없음** (초기 MVP 단계)

### 향후 고려사항
- **Redis 캐싱**:
  - AI 추천 결과 캐싱 (동일 이벤트 타입 + 예산 조합)
  - 다가오는 이벤트 목록 (1시간 TTL)
- **Database Indexing**:
  - events.user_id, events.event_date, events.is_active
  - event_reminders.event_id, event_reminders.is_active
  - reminder_logs.event_id, reminder_logs.sent_at
- **페이징**:
  - 이벤트 목록 조회 (Pageable)
  - 선물 목록 조회 (Pageable)

---

## 10. 배포 아키텍처 (향후 계획)

```
┌────────────────────────────────────────────────┐
│            Client (React)                      │
│  - Vercel / Netlify                            │
└────────────────┬───────────────────────────────┘
                 │ HTTPS
┌────────────────┴───────────────────────────────┐
│            Nginx (Reverse Proxy)               │
│  - SSL/TLS 인증서                               │
│  - Load Balancer (선택사항)                     │
└────────────────┬───────────────────────────────┘
                 │
┌────────────────┴───────────────────────────────┐
│         Spring Boot Application                │
│  - AWS EC2 / Docker Container                  │
│  - Port 8080                                   │
└────────────────┬───────────────────────────────┘
                 │
┌────────────────┴───────────────────────────────┐
│         PostgreSQL Database                    │
│  - AWS RDS                                     │
│  - Port 5432                                   │
└────────────────────────────────────────────────┘

Additional Services:
- AWS S3: 파일 업로드 (선택사항)
- AWS SES: 이메일 발송
- GitHub Actions: CI/CD
- Spring Boot Actuator: 모니터링
```

---

## 11. 보안 고려사항

### 현재 적용
- BCrypt 비밀번호 암호화
- JWT 토큰 기반 인증
- CORS 설정 (허용 도메인 제한)
- SQL Injection 방지 (JPA Parameterized Query)

### 향후 추가 예정
- HTTPS 강제 (배포 시)
- Rate Limiting (API 호출 제한)
- XSS 방지 (입력값 검증 및 이스케이프)
- CSRF 토큰 (필요 시)
- OAuth 2.0 (소셜 로그인)

---

## 12. 모니터링 및 로깅

### 로그 관리
- **Logback** (Spring Boot 기본 로거)
- 로그 레벨: INFO (운영), DEBUG (개발)
- 로그 파일 로테이션 (일별)

### 모니터링 엔드포인트
- Spring Boot Actuator:
  - `/actuator/health` - 헬스 체크
  - `/actuator/metrics` - 메트릭 정보
  - `/actuator/info` - 애플리케이션 정보

### 향후 확장
- ELK Stack (Elasticsearch, Logstash, Kibana)
- Prometheus + Grafana
- APM 도구 (New Relic, Datadog)

---

## 13. 트랜잭션 관리

### 기본 원칙
```java
@Service
@Transactional(readOnly = true) // 기본: 읽기 전용
public class EventService {

    @Transactional // 쓰기 작업만 명시
    public EventDto.Response createEvent(EventDto.Request request, Long userId) {
        // 트랜잭션 범위 내에서 실행
    }

    public List<EventDto.Response> getEvents(Long userId) {
        // readOnly=true (성능 최적화)
    }
}
```

### 트랜잭션 전파
- **REQUIRED** (기본): 기존 트랜잭션 사용 또는 새로 생성
- **REQUIRES_NEW**: 항상 새 트랜잭션 생성 (독립적 처리 필요 시)

---

## 14. 요약

Day Memory 백엔드는 **Spring Boot 3.x + PostgreSQL** 기반의 REST API 서버로,
**JWT 인증**, **스케줄링 리마인더**, **AI 선물 추천**, **N+1 최적화**를 핵심 기능으로 합니다.

- **계층화 아키텍처**: Controller → Service → Repository → Database
- **보안**: Spring Security + JWT, BCrypt
- **스케줄링**: @Scheduled (매일 오전 9시)
- **외부 연동**: JavaMailSender (이메일), OpenAI/Claude (AI)
- **성능 최적화**: fetch join, 인덱싱, 트랜잭션 관리
- **확장 가능성**: 캐싱(Redis), 파일 업로드(S3), 마이크로서비스 전환 고려

---

**작성일**: 2025-01-10
**버전**: 1.0
