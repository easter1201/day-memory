# 백엔드 기술 요약 문서

## 개요

Day Memory 프로젝트의 백엔드 기술 스택, 아키텍처, 개발 환경, 그리고 공통 개발 규칙을 요약한 문서입니다.

---

## 1. 사용 기술 스택

### 핵심 기술
- **언어**: Java 17 (LTS)
- **프레임워크**: Spring Boot 3.x
- **빌드 도구**: Maven 3.8+
- **데이터베이스**: PostgreSQL 14+
- **ORM**: Spring Data JPA (Hibernate)

### 인증 및 보안
- **인증**: Spring Security + JWT (JSON Web Token)
- **비밀번호 암호화**: BCrypt
- **CORS**: CorsConfig 설정
- **소셜 로그인**: OAuth 2.0 (Google, Kakao - 선택사항)

### 알림 및 외부 연동
- **이메일**: JavaMailSender (SMTP)
  - Gmail SMTP 또는 AWS SES
- **AI 통합**: OpenAI API 또는 Anthropic Claude API
- **스케줄링**: Spring `@Scheduled` 어노테이션

### 문서화 및 테스트
- **API 문서화**: Springdoc OpenAPI (Swagger)
- **테스트**: JUnit 5, Mockito, AssertJ
- **통합 테스트**: @SpringBootTest, MockMvc

### 개발 도구
- **IDE**: IntelliJ IDEA Ultimate
- **API 테스트**: Postman
- **데이터베이스 도구**: DBeaver, pgAdmin
- **버전 관리**: Git, GitHub

---

## 2. 주요 아키텍처 개요

### 계층화 아키텍처 (Layered Architecture)

```
┌──────────────────────────────────────────┐
│         Presentation Layer               │
│  - Controller (REST API)                 │
│  - DTO (Request/Response)                │
│  - Exception Handler                     │
└───────────────┬──────────────────────────┘
                │
┌───────────────┴──────────────────────────┐
│         Business Logic Layer             │
│  - Service (비즈니스 로직)                │
│  - Domain Model (엔티티)                  │
└───────────────┬──────────────────────────┘
                │
┌───────────────┴──────────────────────────┐
│         Data Access Layer                │
│  - Repository (Spring Data JPA)          │
│  - Query Methods (fetch join)            │
└───────────────┬──────────────────────────┘
                │
┌───────────────┴──────────────────────────┐
│         Database (PostgreSQL)            │
└──────────────────────────────────────────┘
```

### 폴더 구조

```
src/main/java/com/daymemory/
├── DayMemoryApplication.java          # Main 클래스
├── config/                             # 설정 클래스
│   ├── JpaConfig.java                 # JPA Auditing 설정
│   ├── CorsConfig.java                # CORS 설정
│   ├── SecurityConfig.java            # Spring Security 설정
│   └── SwaggerConfig.java             # Swagger 설정
├── controller/                         # REST API 컨트롤러
│   ├── EventController.java
│   ├── GiftItemController.java
│   └── UserController.java
├── service/                            # 비즈니스 로직
│   ├── EventService.java
│   ├── GiftItemService.java
│   ├── ReminderService.java           # 스케줄러
│   ├── EmailService.java
│   └── AIRecommendationService.java
├── domain/
│   ├── entity/                         # JPA 엔티티
│   │   ├── BaseEntity.java            # 공통 필드 (createdAt, updatedAt)
│   │   ├── User.java
│   │   ├── Event.java
│   │   ├── EventReminder.java
│   │   ├── GiftItem.java
│   │   └── ReminderLog.java
│   ├── repository/                     # JPA Repository
│   │   ├── UserRepository.java
│   │   ├── EventRepository.java
│   │   ├── EventReminderRepository.java
│   │   ├── GiftItemRepository.java
│   │   └── ReminderLogRepository.java
│   └── dto/                            # Data Transfer Object
│       ├── EventDto.java
│       ├── GiftItemDto.java
│       └── UserDto.java
├── security/                           # 보안 관련
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── exception/                          # 예외 처리
│   ├── CustomException.java
│   ├── ErrorCode.java
│   └── GlobalExceptionHandler.java
└── util/                               # 유틸리티
    ├── ResponseWrapper.java
    └── EmailTemplateUtil.java

src/main/resources/
├── application.properties              # 기본 설정
├── application-dev.properties          # 개발 환경
├── application-prod.properties         # 운영 환경
└── templates/                          # 이메일 템플릿
    └── reminder-email.html

src/test/java/com/daymemory/
├── repository/                         # Repository 테스트
├── service/                            # Service 테스트
└── controller/                         # Controller 테스트
```

### 인증 흐름

```
Client → [Login Request] → Controller
         ↓
     UserService (인증 확인)
         ↓
     JwtTokenProvider (JWT 생성)
         ↓
     [Access Token + Refresh Token] → Client

Client → [API Request + JWT] → JwtAuthenticationFilter
         ↓
     JWT 검증 및 사용자 인증 정보 설정
         ↓
     Controller → Service → Repository
```

### 스케줄링 처리

```
@Scheduled(cron = "0 0 9 * * ?")  # 매일 오전 9시
         ↓
    ReminderService.sendDailyReminders()
         ↓
    향후 365일 이벤트 조회
         ↓
    활성 리마인더 필터링
         ↓
    중복 발송 체크
         ↓
    EmailService.sendEmail()
         ↓
    ReminderLog 저장
```

---

## 3. 개발 환경 및 빌드 도구

### 로컬 개발 환경
- **JDK**: 17+ (OpenJDK 또는 Oracle JDK)
- **Maven**: 3.8+
- **PostgreSQL**: 14+
- **포트**:
  - Backend: 8080
  - PostgreSQL: 5432

### 환경 변수 (.env 또는 application.properties)

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/daymemory
spring.datasource.username=daymemory_user
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=your-secret-key
jwt.access-token-expiration=3600000
jwt.refresh-token-expiration=604800000

# Email (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# AI API
openai.api.key=your-openai-api-key
anthropic.api.key=your-claude-api-key
```

### 빌드 및 실행

```bash
# 빌드
mvn clean install

# 실행
mvn spring-boot:run

# 테스트
mvn test

# 패키징
mvn package
```

---

## 4. 데이터 흐름 및 테스트 전략

### 요청/응답 흐름

```
Client (React)
    ↓ HTTP Request (JSON)
Controller (@RestController)
    ↓ DTO Validation
Service (@Service)
    ↓ 비즈니스 로직
Repository (Spring Data JPA)
    ↓ SQL Query (fetch join)
Database (PostgreSQL)
    ↑ Entity
Repository
    ↑ Entity → DTO 변환
Service
    ↑ DTO
Controller
    ↑ ResponseEntity<DTO>
Client (React)
```

### N+1 문제 해결 전략

모든 Repository 쿼리에 `LEFT JOIN FETCH` 적용:

```java
@Query("SELECT DISTINCT e FROM Event e " +
       "LEFT JOIN FETCH e.user " +
       "LEFT JOIN FETCH e.reminders " +
       "WHERE e.user.id = :userId")
List<Event> findByUserIdWithUserAndReminders(@Param("userId") Long userId);
```

### 테스트 전략

#### 1. Repository 테스트
```java
@SpringBootTest
@Transactional
class EventRepositoryTest {
    // N+1 문제 검증
    // fetch join 동작 확인
}
```

#### 2. Service 테스트
```java
@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    // 비즈니스 로직 단위 테스트
}
```

#### 3. Controller 테스트
```java
@WebMvcTest(EventController.class)
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    // API 엔드포인트 테스트
}
```

---

## 5. 공통 개발 규칙

### 응답 형식 (성공)

```json
{
  "id": 1,
  "title": "발렌타인데이",
  "eventDate": "2025-02-14",
  "dDay": 45,
  "reminders": [...]
}
```

### 응답 형식 (에러)

```json
{
  "status": 400,
  "code": "EVENT_NOT_FOUND",
  "message": "해당 이벤트를 찾을 수 없습니다.",
  "timestamp": "2025-01-10T10:30:00"
}
```

### 예외 처리

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(ErrorResponse.of(e.getErrorCode()));
    }
}
```

### 커스텀 예외 클래스

```java
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

### DTO 패턴

- **Request DTO**: 클라이언트 → 서버 (입력 데이터)
- **Response DTO**: 서버 → 클라이언트 (출력 데이터)
- Entity를 직접 노출하지 않음

```java
public class EventDto {
    @Getter
    @Builder
    public static class Request {
        private String title;
        private LocalDate eventDate;
        private List<Integer> reminderDays;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private Long dDay;

        public static Response from(Event event) {
            // Entity → DTO 변환
        }
    }
}
```

### 트랜잭션 관리

```java
@Service
@Transactional(readOnly = true)  // 기본적으로 읽기 전용
public class EventService {

    @Transactional  // 쓰기 작업만 명시
    public EventDto.Response createEvent(EventDto.Request request) {
        // ...
    }
}
```

---

## 6. 향후 확장 및 유지보수 고려사항

### API 문서 자동화
- Springdoc OpenAPI (Swagger UI)
- `/swagger-ui/index.html` 접근
- API 명세 자동 생성

### 성능 최적화
- **쿼리 최적화**: fetch join, @EntityGraph
- **인덱스 설정**: 자주 조회되는 컬럼
- **캐싱**: Redis (선택사항)
- **페이징**: 대량 데이터 조회 시

### 보안 강화
- HTTPS 설정
- SQL Injection 방지 (Parameterized Query)
- XSS 방지 (입력값 검증)
- CSRF 토큰
- Rate Limiting

### 배포 및 운영
- **컨테이너화**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **클라우드**: AWS (EC2, RDS, S3)
- **모니터링**: Spring Boot Actuator, Prometheus, Grafana
- **로깅**: Logback, ELK Stack

### 확장 가능성
- **마이크로서비스**: 추후 서비스 분리 고려
- **메시지 큐**: RabbitMQ, Kafka (이벤트 기반 아키텍처)
- **파일 업로드**: AWS S3 연동
- **푸시 알림**: Firebase Cloud Messaging (FCM)
- **SMS 알림**: Twilio, AWS SNS

---

## 7. 개발 가이드라인

### 코딩 컨벤션
- Java Code Conventions 준수
- 클래스명: PascalCase
- 메서드/변수명: camelCase
- 상수: UPPER_SNAKE_CASE

### Git 브랜치 전략
- `main`: 프로덕션
- `develop`: 개발
- `feature/*`: 기능 개발
- `hotfix/*`: 긴급 수정

### 커밋 메시지 규칙
```
feat: Add user authentication
fix: Resolve N+1 query issue
refactor: Improve service layer structure
docs: Update API documentation
test: Add unit tests for EventService
```

### PR (Pull Request) 규칙
- 코드 리뷰 필수
- 테스트 통과 확인
- 충돌 해결 후 머지

---

## 8. 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Data JPA 공식 문서](https://spring.io/projects/spring-data-jpa)
- [Spring Security 공식 문서](https://spring.io/projects/spring-security)
- [PostgreSQL 공식 문서](https://www.postgresql.org/docs/)
- [Springdoc OpenAPI](https://springdoc.org/)
