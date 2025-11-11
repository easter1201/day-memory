# 백엔드 프로젝트 개발 체크리스트

## 📌 전체 진행 상황

- **완료**: 154개
- **진행중**: 0개
- **대기**: 15개
- **전체**: 169개
- **진행률**: 91.1%

---

## ✅ Phase 1: 핵심 기능 (MVP)

### 👤 사용자 인증 및 관리

#### 기본 인증 API
- [x] 사용자 회원가입 API (POST /api/users/signup)
- [x] 사용자 로그인 API (POST /api/users/login)
- [x] JWT 토큰 재발급 API (POST /api/users/refresh)
- [x] 내 정보 조회 API (GET /api/users/me)
- [x] 내 정보 수정 API (PUT /api/users/me)
- [x] 비밀번호 변경 API (PUT /api/users/password)

#### 보안 설정
- [x] Spring Security 설정 (SecurityConfig)
- [x] JwtTokenProvider 구현 (JWT 생성/검증)
- [x] JwtAuthenticationFilter 구현 (JWT 필터)
- [x] CustomUserDetailsService 구현 (사용자 인증 정보 로드)
- [x] BCrypt 비밀번호 암호화
- [x] SecurityContext 인증 정보 설정

#### 엔티티 및 Repository
- [x] User 엔티티 설계 (email, password, name)
- [x] UserRepository 구현 (findByEmail, existsByEmail)
- [x] DTO 설계 (SignupRequest, LoginRequest, LoginResponse)

---

### 📅 이벤트 / 기념일 관리

#### 기본 CRUD API
- [x] 이벤트 생성 API (POST /api/events)
- [x] 이벤트 목록 조회 API (GET /api/events)
- [x] 특정 이벤트 조회 API (GET /api/events/{eventId})
- [x] 이벤트 수정 API (PUT /api/events/{eventId})
- [x] 이벤트 삭제(비활성화) API (DELETE /api/events/{eventId})
- [x] 다가오는 이벤트 조회 API (GET /api/events/upcoming)

#### 고급 기능
- [x] 이벤트 추적 토글 API (PUT /api/events/{eventId}/tracking)
- [x] D-Day 계산 로직 구현
- [x] 반복 이벤트 자동 생성 로직 (is_recurring)
- [x] 이벤트 타입별 필터링 API (GET /api/events?type={type})
- [x] EventType enum 확장 (28+ 타입)
  - [x] 생일, 기념일 (100/200/300일, 1주년)
  - [x] 월별 커플 데이 (다이어리/발렌타인/화이트/블랙/로즈/키스/실버/그린/뮤직/와인/무비/허그)
  - [x] 공휴일 (빼빼로데이, 크리스마스, 새해)
  - [x] 사용자 정의 (휴가, 커스텀)

#### 엔티티 및 Repository
- [x] Event 엔티티 설계
  - [x] title, description, eventDate, eventType
  - [x] isRecurring, isActive, isTracking 필드
  - [x] reminders OneToMany 관계
- [x] EventRepository 구현
  - [x] N+1 문제 해결 (LEFT JOIN FETCH user, reminders)
  - [x] findByUserIdAndIsActiveTrue
  - [x] findByIdWithUserAndReminders
  - [x] findUpcomingEvents
  - [x] findTrackingEventsBetweenDates

---

### ⏰ 리마인더 시스템

#### 리마인더 설정
- [x] EventReminder 엔티티 생성 (유연한 일수 설정)
- [x] 리마인더 설정 추가/수정 API (PUT /api/events/{eventId}/reminders)
- [x] 리마인더 활성화/비활성화 로직 (isActive 필드)
- [x] 유연한 리마인더 일수 설정 (30, 14, 7, 3, 1일 등)
- [x] EventReminderRepository 구현

#### 스케줄러 및 발송
- [x] 스케줄러 구현 (@Scheduled - 매일 오전 9시)
- [x] 향후 365일 이벤트 조회 로직
- [x] 오늘 발송할 리마인더 필터링
- [x] 중복 발송 방지 로직 (24시간 이내)
- [x] JavaMailSender 설정 (application.yml)
- [x] 이메일 발송 로직 구현 (EmailService)
- [x] 이메일 템플릿 디자인 (HTML 템플릿)
- [x] SMTP 설정 (Gmail)

#### 로그 및 모니터링
- [x] ReminderLog 엔티티 생성
  - [x] event_id, days_before_event, sent_at, status
- [x] ReminderLogRepository 구현
- [x] 리마인더 발송 로그 기록
- [x] 리마인더 발송 이력 조회 API (GET /api/reminders/logs)
- [x] 실패한 리마인더 재발송 API (POST /api/reminders/retry/{reminderLogId})
- [x] 즉시 리마인더 발송 기능 (POST /api/reminders/immediate/{eventId})

---

### 🎁 선물 관리

#### 기본 CRUD API
- [x] 선물 아이템 생성 API (POST /api/gifts)
- [x] 선물 목록 조회 API (GET /api/gifts)
- [x] 특정 이벤트 선물 조회 API (GET /api/gifts/event/{eventId})
- [x] 선물 아이템 수정 API (PUT /api/gifts/{giftId})
- [x] 선물 아이템 삭제 API (DELETE /api/gifts/{giftId})
- [x] 선물 구매 완료 표시 API (PUT /api/gifts/{giftId}/purchase)

#### 고급 기능
- [x] 미구매 선물 목록 조회 API (GET /api/gifts?purchased=false)
- [x] 카테고리별 선물 조회 API (GET /api/gifts?category={category})
- [x] 선물 검색 기능 (GET /api/gifts/search?keyword={keyword})
- [x] 선물 이미지 업로드 (로컬 파일 시스템, AWS S3 연동 준비)

#### 엔티티 및 Repository
- [x] GiftItem 엔티티 설계
  - [x] name, description, price, url
  - [x] category, isPurchased
  - [x] user, event 관계 (ManyToOne)
- [x] GiftItemRepository 구현
  - [x] N+1 문제 해결 (LEFT JOIN FETCH user, event)
- [x] GiftCategory enum 정의
  - [x] FLOWER, JEWELRY, COSMETICS, FASHION, ELECTRONICS
  - [x] FOOD, EXPERIENCE, BOOK, HOBBY, OTHER

---

## ✅ Phase 2: 확장 기능

### 🤖 AI 기반 선물 추천

#### API 연동
- [x] OpenAI API 또는 Claude API 연동
- [x] API 키 설정 (application.properties)
- [x] AIRecommendationService 생성
- [x] AI 추천 요청 API (POST /api/gifts/recommend)
- [x] 프롬프트 엔지니어링 (기념일 유형별)

#### 추천 로직
- [x] 컨텍스트 빌더 (이벤트 정보, 관계, 예산)
- [x] 사용자 저장 선물 우선 표시 로직
- [x] AI 응답 파싱 (선물명, 설명, 이유)
- [x] 추천 결과 포맷 (JSON)
- [x] 추천 이유 포함하여 응답
- [ ] 추천 결과 캐싱 (Redis - 선택)

#### 예외 처리
- [x] API 호출 실패 처리 (AI_SERVICE_UNAVAILABLE)
- [x] 타임아웃 처리 (AI_REQUEST_FAILED)
- [x] 대체 응답 제공 (fallback)

---

### 📊 대시보드 및 통계

#### 대시보드 API
- [x] 대시보드 요약 정보 API (GET /api/dashboard)
  - [x] 다가오는 이벤트 요약 (upcoming events count)
  - [x] 미구매 선물 개수 표시
  - [x] 최근 리마인더 발송 현황
  - [x] 이번 달 이벤트 개수

#### 통계 API
- [x] 월별 이벤트 통계 API (GET /api/statistics/events)
  - [x] 월별 이벤트 수 집계
  - [x] 이벤트 타입별 분포
- [x] 선물 구매 내역 통계 API (GET /api/statistics/gifts)
  - [x] 총 구매 선물 수
  - [x] 카테고리별 구매 통계
  - [x] 총 지출 금액
- [x] 리마인더 발송 통계 API (GET /api/statistics/reminders)
  - [x] 발송 성공/실패 건수
  - [x] 일별 발송 통계
- [x] 캘린더 뷰 데이터 조회 API (GET /api/calendar)
  - [x] 특정 월의 모든 이벤트 조회
  - [x] 날짜별 이벤트 그룹핑

---

### 🔐 추가 인증 기능

#### 소셜 로그인
- [x] OAuth2 클라이언트 설정 (Spring Security)
- [x] Google OAuth 연동 (POST /api/auth/google)
  - [x] Google API Console 설정 (환경변수)
  - [x] Redirect URI 설정
- [x] Kakao OAuth 연동 (POST /api/auth/kakao)
  - [x] Kakao Developers 설정 (환경변수)
  - [x] Redirect URI 설정
- [x] 소셜 로그인 콜백 처리
- [x] JWT 토큰 발급 (소셜 로그인 성공 시)

#### 계정 관리
- [x] 이메일 인증 기능
  - [x] 회원가입 시 인증 메일 발송
  - [x] 이메일 인증 토큰 생성
  - [x] 인증 완료 처리 API
- [x] 비밀번호 찾기/재설정 기능
  - [x] 비밀번호 재설정 요청 API
  - [x] 재설정 링크 이메일 발송
  - [x] 재설정 토큰 검증
  - [x] 새 비밀번호 설정 API

---

## ✅ Phase 3: 공통 인프라 및 시스템

### 예외 처리 및 응답

#### CustomException 구조
- [x] CustomException 클래스 생성
- [x] ErrorCode enum 정의
  - [x] USER_ (7개: NOT_FOUND, ALREADY_EXISTS, INVALID_PASSWORD, INVALID_TOKEN, EXPIRED_TOKEN, UNAUTHORIZED, FORBIDDEN)
  - [x] EVENT_ (5개: NOT_FOUND, DATE_INVALID, TYPE_INVALID, ACCESS_DENIED, REMINDER_DAYS_INVALID)
  - [x] REMINDER_ (3개: NOT_FOUND, ALREADY_SENT, EMAIL_SEND_FAILED)
  - [x] GIFT_ (3개: NOT_FOUND, ACCESS_DENIED, CATEGORY_INVALID)
  - [x] AI_ (2개: SERVICE_UNAVAILABLE, REQUEST_FAILED)
  - [x] 공통 (4개: VALIDATION_ERROR, INVALID_REQUEST, SERVER_INTERNAL_ERROR, DATABASE_ERROR)
- [x] GlobalExceptionHandler 구현 (@RestControllerAdvice)
  - [x] CustomException 처리
  - [x] MethodArgumentNotValidException 처리 (@Valid)
  - [x] 예상치 못한 예외 처리

#### 응답 형식
- [x] ErrorResponse DTO 설계 (status, code, message, timestamp)
- [x] ValidationErrorResponse DTO 설계 (+ errors 배열)
- [x] FieldErrorDetail 클래스 (field, rejectedValue, message)
- [x] 일관된 에러 응답 형식 적용
- [x] HTTP 상태 코드 명확한 매핑

---

### 검증 및 보안

#### Bean Validation
- [x] DTO에 @Valid 어노테이션 적용
- [x] 필수값 검증 (@NotNull, @NotBlank)
- [x] 이메일 형식 검증 (@Email)
- [x] 문자열 길이 검증 (@Size)
- [x] 날짜 검증 (@FutureOrPresent)
- [x] 숫자 검증 (@Positive, @Min, @Max)
- [x] 커스텀 Validator 구현 (@ValidEventDate)

#### 보안 설정
- [x] SQL Injection 방지 확인 (JPA Parameterized Query)
- [x] XSS 방지 처리 (HtmlUtils.htmlEscape)
- [x] Rate Limiting (API 호출 제한)
  - [x] 메모리 기반 Rate Limit (분당 100회)
  - [x] IP별 호출 제한 설정
- [ ] HTTPS 강제 설정 (배포 시)

---

### 문서화

#### Swagger/Springdoc OpenAPI
- [x] Springdoc OpenAPI 의존성 추가 (pom.xml)
  - [x] springdoc-openapi-starter-webmvc-ui 의존성 추가
- [x] SwaggerConfig 설정
  - [x] OpenAPI Bean 생성
  - [x] API 정보 설정 (title, version, description, contact)
  - [x] JWT 인증 설정 (SecurityScheme - bearerAuth)
  - [x] Security Requirement 전역 설정
- [x] API 명세 자동 생성
  - [x] Swagger UI 접근 확인 (/swagger-ui/index.html)
  - [x] OpenAPI JSON 접근 확인 (/v3/api-docs)
- [x] Controller에 @Operation, @ApiResponse 적용
  - [x] UserController API 문서화 (6개 메서드)
  - [x] EventController API 문서화 (8개 메서드)
  - [x] GiftItemController API 문서화 (9개 메서드)
  - [x] ReminderController API 문서화 (4개 메서드)
  - [x] AIRecommendationController API 문서화 (1개 메서드)
  - [x] DashboardController API 문서화 (1개 메서드)
  - [x] StatisticsController API 문서화 (4개 메서드)
  - [x] OAuthController API 문서화 (2개 메서드)
  - [x] VerificationController API 문서화 (4개 메서드)
- [x] DTO에 @Schema 적용 (설명 추가)
  - [x] Request DTO 문서화 (SignupRequest, LoginRequest 등 - 35개 클래스)
  - [x] Response DTO 문서화 (LoginResponse, EventResponse 등)
  - [x] 필드별 description, example 추가 (~130개 필드)
- [x] 예제 요청/응답 추가
  - [x] @Schema example 활용하여 실제 사용 예시 추가
  - [x] 각 API별 성공/실패 케이스 예시 (ApiResponses)
- [x] API 그룹핑 및 태그 정리
  - [x] @Tag 어노테이션으로 Controller 그룹화 (9개 태그)
  - [x] 태그별 설명 추가

---

### 로깅 및 모니터링

#### Logback 설정
- [x] Logback 설정 파일 작성 (logback-spring.xml)
- [x] 로그 레벨 설정 (INFO: 운영, DEBUG: 개발)
- [x] 로그 파일 로테이션 (일별)
- [x] 요청/응답 로깅 인터셉터 (LoggingInterceptor)
- [x] 에러 로그 분류 (ERROR: 5xx, WARN: 4xx)
- [x] GlobalExceptionHandler 로깅 추가

#### Spring Boot Actuator
- [x] Spring Boot Actuator 의존성 추가
- [x] Actuator 엔드포인트 설정
  - [x] /actuator/health - 헬스 체크
  - [x] /actuator/metrics - 메트릭 정보
  - [x] /actuator/info - 애플리케이션 정보
- [x] 성능 모니터링 엔드포인트 활성화
- [x] 보안 설정 (Actuator 접근 제한)

---

### 기타 설정

#### 공통 설정
- [x] CORS 설정 (CorsConfig)
  - [x] 허용 Origin 설정
  - [x] 허용 Methods 설정
  - [x] 허용 Headers 설정
- [x] JPA Auditing 설정 (BaseEntity)
  - [x] createdAt, updatedAt 자동 관리
  - [x] @EnableJpaAuditing
- [x] 환경별 설정 분리
  - [x] application-dev.yml (개발)
  - [x] application-prod.yml (운영)
  - [x] Spring Profiles 설정 (dev/prod)
- [x] 민감 정보 환경 변수화
  - [x] JWT secret key
  - [x] Database password
  - [x] Email password
  - [x] AI API keys
  - [x] OAuth credentials
  - [x] .env.example 파일 생성
  - [x] .gitignore 업데이트 (logs, uploads, .env)

---

## ✅ Phase 4: 테스트

### Repository 테스트

#### 단위 테스트
- [x] UserRepository 테스트 (10개 테스트)
  - [x] findByEmail 테스트 (성공/실패)
  - [x] existsByEmail 테스트 (존재/미존재)
  - [x] OAuth 제공자별 조회
  - [x] 이메일 인증 상태 확인
  - [x] 비밀번호 업데이트
  - [x] 유니크 제약조건 검증
- [x] EventRepository 테스트 (12개 테스트)
  - [x] fetch join 동작 확인 (User, Reminders)
  - [x] findByUserIdAndIsActiveTrue 테스트
  - [x] findUpcomingEvents 테스트
  - [x] 이벤트 타입별 필터링
  - [x] 반복 이벤트 조회
  - [x] 추적 이벤트 기간별 조회
  - [x] N+1 문제 해결 검증
- [x] GiftItemRepository 테스트 (15개 테스트)
  - [x] findByUserId 테스트
  - [x] findByEventId 테스트
  - [x] 미구매 선물 필터링
  - [x] 카테고리별 조회
  - [x] 키워드 검색 (이름, 설명)
  - [x] fetch join 검증 (User, Event)
  - [x] 구매 상태 관리
- [x] ReminderLogRepository 테스트 (11개 테스트)
  - [x] 중복 발송 체크 테스트 (24시간)
  - [x] 이벤트별 리마인더 로그 조회
  - [x] 기간별 필터링
  - [x] 실패 리마인더 조회
  - [x] 상태 변경 테스트
- [x] EventReminderRepository 테스트 (12개 테스트)
  - [x] 활성 리마인더 조회
  - [x] 일수별 리마인더 조회
  - [x] 활성화/비활성화 토글
  - [x] Cascade 삭제 검증
- [x] VerificationTokenRepository 테스트 (15개 테스트)
  - [x] 토큰 조회 (성공/실패)
  - [x] 사용자 및 타입별 토큰 조회
  - [x] 토큰 사용 처리
  - [x] 만료 토큰 확인
- [x] N+1 쿼리 검증 테스트
  - [x] EventRepository Fetch Join 검증
  - [x] GiftItemRepository Fetch Join 검증
  - [x] ReminderLogRepository Fetch Join 검증
- [x] 테스트 설정 파일 (application-test.properties)

---

### Service 테스트

#### 단위 테스트 (Mockito)
- [ ] EventService 테스트
  - [ ] createEvent 테스트
  - [ ] getEvent 테스트 (권한 확인)
  - [ ] updateEvent 테스트
  - [ ] deleteEvent 테스트
  - [ ] toggleTracking 테스트
- [ ] GiftItemService 테스트
  - [ ] createGift 테스트
  - [ ] purchaseGift 테스트
- [ ] ReminderService 테스트
  - [ ] sendDailyReminders 테스트
  - [ ] 중복 발송 방지 테스트
- [ ] EmailService 테스트
  - [ ] sendEmail 테스트 (Mock)
- [ ] Mock 객체 활용 (@Mock, @InjectMocks)

---

### Controller 테스트

#### 통합 테스트 (MockMvc)
- [ ] EventController 테스트
  - [ ] POST /api/events (201 Created)
  - [ ] GET /api/events (200 OK)
  - [ ] GET /api/events/{eventId} (200 OK, 404 Not Found)
  - [ ] PUT /api/events/{eventId} (200 OK)
  - [ ] DELETE /api/events/{eventId} (204 No Content)
- [ ] GiftItemController 테스트
  - [ ] POST /api/gifts (201 Created)
  - [ ] GET /api/gifts (200 OK)
  - [ ] PUT /api/gifts/{giftId}/purchase (200 OK)
- [ ] UserController 테스트
  - [ ] POST /api/users/signup (201 Created)
  - [ ] POST /api/users/login (200 OK)
  - [ ] POST /api/users/refresh (200 OK)
- [ ] MockMvc 활용 API 테스트
- [ ] JWT 인증 테스트

---

### 통합 테스트

#### @SpringBootTest
- [ ] 전체 통합 테스트
  - [ ] 애플리케이션 컨텍스트 로드 확인
  - [ ] Bean 주입 확인
- [ ] 스케줄러 동작 테스트
  - [ ] @Scheduled 메서드 실행 확인
- [ ] 트랜잭션 롤백 테스트
  - [ ] @Transactional 동작 확인
- [ ] 예외 처리 테스트
  - [ ] CustomException 발생 및 처리 확인
  - [ ] GlobalExceptionHandler 동작 확인

---

## ✅ Phase 5: 배포 및 운영

### 컨테이너화

#### Docker 설정
- [ ] Dockerfile 작성 (백엔드)
  - [ ] Java 17 베이스 이미지
  - [ ] JAR 파일 복사
  - [ ] 포트 노출 (8080)
- [ ] Docker Compose 설정
  - [ ] Spring Boot 서비스
  - [ ] PostgreSQL 서비스
  - [ ] 네트워크 설정
- [ ] 개발/프로덕션 환경 분리
  - [ ] docker-compose.dev.yml
  - [ ] docker-compose.prod.yml
- [ ] 환경 변수 설정 (.env 파일)

---

### CI/CD

#### GitHub Actions
- [ ] GitHub Actions 워크플로우 작성 (.github/workflows)
- [ ] 자동 테스트 실행 설정
  - [ ] mvn test 실행
  - [ ] 테스트 결과 리포트
- [ ] 자동 빌드 설정
  - [ ] mvn package 실행
  - [ ] Docker 이미지 빌드
- [ ] 자동 배포 스크립트
  - [ ] Docker Hub 푸시
  - [ ] AWS EC2 배포
- [ ] 배포 롤백 전략

---

### 클라우드 배포

#### AWS 설정
- [ ] AWS EC2 인스턴스 설정
  - [ ] 인스턴스 생성 (Ubuntu 또는 Amazon Linux)
  - [ ] 보안 그룹 설정 (8080, 22 포트)
  - [ ] Elastic IP 할당
- [ ] AWS RDS (PostgreSQL) 설정
  - [ ] RDS 인스턴스 생성
  - [ ] 데이터베이스 설정
  - [ ] 보안 그룹 설정 (5432 포트)
- [ ] AWS S3 버킷 생성 (선택)
  - [ ] 파일 업로드용 버킷
  - [ ] IAM 역할 설정
- [ ] 도메인 및 HTTPS 설정
  - [ ] Route 53 DNS 설정
  - [ ] SSL 인증서 발급 (Let's Encrypt)
- [ ] Nginx 리버스 프록시 설정
  - [ ] Nginx 설치
  - [ ] 프록시 설정 (8080 → 80/443)
  - [ ] SSL 설정

---

### 모니터링 및 유지보수

#### 모니터링
- [ ] Spring Boot Actuator 프로덕션 설정
- [ ] 로그 수집 시스템 (ELK Stack - 선택)
  - [ ] Elasticsearch 설정
  - [ ] Logstash 파이프라인
  - [ ] Kibana 대시보드
- [ ] APM 도구 연동 (선택)
  - [ ] New Relic 또는 Datadog

#### 유지보수
- [ ] 백업 및 복구 전략
  - [ ] 데이터베이스 일일 백업
  - [ ] S3 백업 저장
  - [ ] 복구 절차 문서화
- [ ] 성능 튜닝
  - [ ] 쿼리 최적화
  - [ ] 인덱스 추가
  - [ ] 캐싱 전략 (Redis)

---

## 📝 개발 우선순위 요약

### 🔥 최우선 (1주차)
1. **사용자 인증 시스템 구현**
   - Spring Security + JWT 설정
   - 회원가입/로그인 API
   - JwtTokenProvider, JwtAuthenticationFilter
2. **이메일 리마인더 발송 기능 완성**
   - JavaMailSender 설정
   - 이메일 템플릿 작성
   - 실제 이메일 발송 테스트
3. **예외 처리 및 공통 응답 형식 적용**
   - CustomException, ErrorCode
   - GlobalExceptionHandler
   - 모든 Service에 적용

### ⭐ 우선 (2-3주차)
4. **AI 선물 추천 기능 구현**
   - OpenAI/Claude API 연동
   - 프롬프트 엔지니어링
   - 사용자 저장 선물 우선 표시
5. **대시보드 및 통계 API**
   - 다가오는 이벤트 요약
   - 선물 구매 통계
   - 캘린더 뷰
6. **Swagger API 문서화**
   - Springdoc OpenAPI 설정
   - API 명세 자동 생성
7. **기본 테스트 코드 작성**
   - 핵심 기능 단위 테스트
   - Controller 통합 테스트

### 📌 일반 (4주차 이후)
8. **소셜 로그인 연동**
   - Google OAuth
   - Kakao OAuth
9. **추가 기능 및 UI 개선**
   - 반복 이벤트 자동 생성
   - 선물 이미지 업로드 (S3)
10. **성능 최적화**
    - Redis 캐싱
    - 쿼리 최적화
    - 페이징 적용
11. **배포 및 운영 준비**
    - Docker 컨테이너화
    - CI/CD 구축
    - AWS 배포

---

## 🎯 다음 작업

현재 완료된 기능 (이벤트/선물/리마인더 기본 구조)을 기반으로 다음 작업을 진행하세요:

### 1. **사용자 인증 시스템** 구현 시작
   - [ ] User 엔티티 및 Repository 생성
   - [ ] JWT 토큰 발급 및 검증 로직 (JwtTokenProvider)
   - [ ] Spring Security 설정 (SecurityConfig)
   - [ ] 로그인/회원가입 API (UserController, UserService)
   - [ ] JWT 필터 적용 (JwtAuthenticationFilter)

### 2. **이메일 시스템** 완성
   - [ ] JavaMailSender 설정 (application.properties)
   - [ ] EmailService 구현 (sendEmail 메서드)
   - [ ] 이메일 템플릿 작성 (reminder-email.html)
   - [ ] 실제 이메일 발송 테스트 (Gmail SMTP)
   - [ ] 발송 실패 처리 및 재시도 로직

### 3. **예외 처리** 구조 구축
   - [ ] CustomException 및 ErrorCode 정의
   - [ ] GlobalExceptionHandler 작성
   - [ ] 모든 Service에 예외 처리 적용
   - [ ] DTO Validation 적용 (@Valid)
   - [ ] 일관된 에러 응답 형식 확인

### 4. **테스트 코드** 작성 시작
   - [ ] 핵심 기능부터 단위 테스트 작성 (EventService, GiftItemService)
   - [ ] Repository 테스트 (N+1 확인)
   - [ ] Controller 통합 테스트 (MockMvc)
   - [ ] 통합 테스트 준비 (@SpringBootTest)

---

**작성일**: 2025-01-10
**버전**: 1.0
**참고 문서**: 01_backend_requirements.md, 02_db_schema.md, 03_api_spec.md, 04_architecture.md, 05_exception_policy.md
