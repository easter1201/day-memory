# 백엔드 프로젝트 개발 체크리스트

## 📌 전체 진행 상황

- **완료**: 37개
- **진행중**: 0개
- **대기**: 48개
- **전체**: 85개
- **진행률**: 43.5%

---

## ✅ Phase 1: 핵심 기능 (MVP)

### 👤 사용자 인증 및 관리

#### 기본 인증
- [ ] 사용자 회원가입 API (POST /api/users/signup)
- [ ] 사용자 로그인 API (POST /api/users/login)
- [ ] JWT 토큰 재발급 API (POST /api/users/refresh)
- [ ] 내 정보 조회 API (GET /api/users/me)
- [ ] 내 정보 수정 API (PUT /api/users/me)
- [ ] 비밀번호 변경 API (PUT /api/users/password)

#### 보안 설정
- [ ] Spring Security 설정 (SecurityConfig)
- [ ] JwtTokenProvider 구현
- [ ] JwtAuthenticationFilter 구현
- [ ] CustomUserDetailsService 구현
- [ ] BCrypt 비밀번호 암호화

---

### 📅 이벤트 / 기념일 관리

#### 기본 CRUD
- [x] 이벤트 생성 API (POST /api/events)
- [x] 이벤트 목록 조회 API (GET /api/events)
- [x] 특정 이벤트 조회 API (GET /api/events/{eventId})
- [x] 이벤트 수정 API (PUT /api/events/{eventId})
- [x] 이벤트 삭제(비활성화) API (DELETE /api/events/{eventId})
- [x] 다가오는 이벤트 조회 API (GET /api/events/upcoming)

#### 고급 기능
- [x] 이벤트 추적 토글 API (PUT /api/events/{eventId}/tracking)
- [x] D-Day 계산 로직 구현
- [ ] 반복 이벤트 자동 생성 로직
- [ ] 이벤트 타입별 필터링 기능
- [x] EventType enum 확장 (월별 커플 데이 등)

#### 엔티티 및 Repository
- [x] Event 엔티티 설계
- [x] EventReminder 엔티티 생성
- [x] EventRepository 구현
- [x] EventReminderRepository 구현
- [x] N+1 문제 해결 (fetch join)

---

### ⏰ 리마인더 시스템

#### 리마인더 설정
- [x] 리마인더 설정 추가/수정 API (PUT /api/events/{eventId}/reminders)
- [x] 리마인더 활성화/비활성화 로직
- [x] 유연한 리마인더 일수 설정 (30, 14, 7, 3, 1일 등)

#### 스케줄러 및 발송
- [x] 스케줄러 구현 (@Scheduled - 매일 오전 9시)
- [x] 향후 365일 이벤트 조회 로직
- [x] 오늘 발송할 리마인더 필터링
- [x] 중복 발송 방지 로직 (24시간 이내)
- [ ] 이메일 리마인더 발송 (JavaMailSender)
- [ ] 이메일 템플릿 디자인

#### 로그 및 모니터링
- [x] ReminderLog 엔티티 생성
- [x] ReminderLogRepository 구현
- [x] 리마인더 발송 로그 기록
- [ ] 리마인더 발송 이력 조회 API
- [ ] 실패한 리마인더 재발송 API
- [x] 즉시 리마인더 발송 기능 (테스트용)

---

### 🎁 선물 관리

#### 기본 CRUD
- [x] 선물 아이템 생성 API (POST /api/gifts)
- [x] 선물 목록 조회 API (GET /api/gifts)
- [x] 특정 이벤트 선물 조회 API (GET /api/gifts/event/{eventId})
- [x] 선물 아이템 수정 API (PUT /api/gifts/{giftId})
- [x] 선물 아이템 삭제 API (DELETE /api/gifts/{giftId})
- [x] 선물 구매 완료 표시 API (PUT /api/gifts/{giftId}/purchase)

#### 고급 기능
- [x] 미구매 선물 목록 조회 API
- [ ] 카테고리별 선물 조회 API
- [ ] 선물 검색 기능
- [ ] 선물 이미지 업로드 (AWS S3)

#### 엔티티 및 Repository
- [x] GiftItem 엔티티 설계
- [x] GiftItemRepository 구현
- [x] N+1 문제 해결 (fetch join)
- [x] GiftCategory enum 정의

---

## ✅ Phase 2: 확장 기능

### 🤖 AI 기반 선물 추천

#### API 연동
- [ ] OpenAI API 또는 Claude API 연동
- [ ] AI 추천 요청 API (POST /api/gifts/recommend)
- [ ] 프롬프트 엔지니어링 (기념일 유형별)

#### 추천 로직
- [ ] 사용자 저장 선물 우선 표시 로직
- [ ] AI 추천 결과 파싱
- [ ] 추천 이유 포함하여 응답
- [ ] 추천 결과 캐싱 (Redis - 선택)

#### Service 구현
- [ ] AIRecommendationService 생성
- [ ] 컨텍스트 빌더 (이벤트 정보, 관계, 예산 등)
- [ ] 예외 처리 (API 호출 실패 시)

---

### 📊 대시보드 및 통계

#### 대시보드
- [ ] 대시보드 요약 정보 API (GET /api/dashboard)
- [ ] 다가오는 이벤트 요약
- [ ] 미구매 선물 개수 표시
- [ ] 최근 리마인더 발송 현황

#### 통계
- [ ] 월별 이벤트 통계 API (GET /api/statistics/events)
- [ ] 선물 구매 내역 통계 API (GET /api/statistics/gifts)
- [ ] 리마인더 발송 통계 API (GET /api/statistics/reminders)
- [ ] 캘린더 뷰 데이터 조회 API (GET /api/calendar)

---

### 🔐 추가 인증 기능

- [ ] 소셜 로그인 (Google OAuth) API
- [ ] 소셜 로그인 (Kakao OAuth) API
- [ ] 이메일 인증 기능
- [ ] 비밀번호 찾기/재설정 기능

---

## ✅ Phase 3: 공통 인프라 및 시스템

### 예외 처리 및 응답
- [ ] CustomException 클래스 생성
- [ ] ErrorCode enum 정의
- [ ] GlobalExceptionHandler 구현 (@RestControllerAdvice)
- [ ] ResponseWrapper 설계 및 적용
- [ ] 일관된 에러 응답 형식

### 검증 및 보안
- [ ] Bean Validation 적용 (@Valid, @NotNull 등)
- [ ] 커스텀 Validator 구현
- [ ] SQL Injection 방지 확인
- [ ] XSS 방지 처리
- [ ] Rate Limiting (API 호출 제한)

### 문서화
- [ ] Swagger/Springdoc OpenAPI 설정
- [ ] API 명세 자동 생성
- [ ] DTO 문서화 (@Schema)
- [ ] 예제 요청/응답 추가

### 로깅 및 모니터링
- [ ] Logback 설정
- [ ] 요청/응답 로깅 인터셉터
- [ ] 에러 로그 분류
- [ ] Spring Boot Actuator 설정
- [ ] 성능 모니터링 엔드포인트

### 기타 설정
- [x] CORS 설정 (CorsConfig)
- [x] JPA Auditing 설정 (BaseEntity)
- [ ] 환경별 설정 분리 (dev, prod)
- [ ] 민감 정보 암호화 (.env)

---

## ✅ Phase 4: 테스트

### Repository 테스트
- [ ] UserRepository 테스트
- [ ] EventRepository 테스트
- [ ] GiftItemRepository 테스트
- [ ] ReminderLogRepository 테스트
- [ ] N+1 쿼리 검증 테스트

### Service 테스트
- [ ] EventService 단위 테스트
- [ ] GiftItemService 단위 테스트
- [ ] ReminderService 단위 테스트
- [ ] EmailService 단위 테스트
- [ ] Mock 객체 활용 테스트

### Controller 테스트
- [ ] EventController 통합 테스트
- [ ] GiftItemController 통합 테스트
- [ ] UserController 통합 테스트
- [ ] MockMvc 활용 API 테스트

### 통합 테스트
- [ ] @SpringBootTest 전체 통합 테스트
- [ ] 스케줄러 동작 테스트
- [ ] 트랜잭션 롤백 테스트
- [ ] 예외 처리 테스트

---

## ✅ Phase 5: 배포 및 운영

### 컨테이너화
- [ ] Dockerfile 작성 (백엔드)
- [ ] Docker Compose 설정
- [ ] 개발/프로덕션 환경 분리
- [ ] 환경 변수 설정

### CI/CD
- [ ] GitHub Actions 워크플로우 작성
- [ ] 자동 테스트 실행 설정
- [ ] 자동 빌드 및 배포 스크립트
- [ ] 배포 롤백 전략

### 클라우드 배포
- [ ] AWS EC2 인스턴스 설정
- [ ] AWS RDS (PostgreSQL) 설정
- [ ] AWS S3 버킷 생성 (선택)
- [ ] 도메인 및 HTTPS 설정
- [ ] Nginx 리버스 프록시 설정

### 모니터링 및 유지보수
- [ ] Spring Boot Actuator 프로덕션 설정
- [ ] 로그 수집 시스템 (ELK Stack - 선택)
- [ ] APM 도구 연동 (선택)
- [ ] 백업 및 복구 전략
- [ ] 성능 튜닝

---

## 📝 개발 우선순위 요약

### 최우선 (1주차)
1. 사용자 인증 시스템 구현
2. 이메일 리마인더 발송 기능 완성
3. 예외 처리 및 공통 응답 형식 적용

### 우선 (2-3주차)
4. AI 선물 추천 기능 구현
5. 대시보드 및 통계 API
6. Swagger API 문서화
7. 기본 테스트 코드 작성

### 일반 (4주차 이후)
8. 소셜 로그인 연동
9. 추가 기능 및 UI 개선
10. 성능 최적화
11. 배포 및 운영 준비

---

## 🎯 다음 작업

현재 완료된 기능을 기반으로 다음 작업을 진행하세요:

1. **사용자 인증 시스템** 구현 시작
   - JWT 토큰 발급 및 검증 로직
   - Spring Security 설정

2. **이메일 시스템** 완성
   - JavaMailSender 설정
   - 이메일 템플릿 작성
   - 실제 이메일 발송 테스트

3. **예외 처리** 구조 구축
   - CustomException 및 ErrorCode 정의
   - GlobalExceptionHandler 작성
   - 모든 Service에 적용

4. **테스트 코드** 작성 시작
   - 핵심 기능부터 단위 테스트 작성
   - 통합 테스트 준비
