# 백엔드 기능 요구사항 정의서

## 개요

Day Memory 프로젝트의 백엔드 기능을 API 중심으로 정의한 요구사항 문서입니다.

---

## 기능 요구사항 목록

### 👤 사용자 인증 및 관리

| ID     | 기능 설명           | 우선순위 | 관련 API                      |
|--------|-------------------|---------|-------------------------------|
| BE-001 | 사용자 회원가입      | 높음    | POST /api/users/signup        |
| BE-002 | 사용자 로그인        | 높음    | POST /api/users/login         |
| BE-003 | JWT 토큰 재발급     | 높음    | POST /api/users/refresh       |
| BE-004 | 내 정보 조회        | 중간    | GET /api/users/me             |
| BE-005 | 내 정보 수정        | 중간    | PUT /api/users/me             |
| BE-006 | 비밀번호 변경       | 중간    | PUT /api/users/password       |
| BE-007 | 소셜 로그인 (Google)| 낮음    | GET /api/auth/google          |
| BE-008 | 소셜 로그인 (Kakao) | 낮음    | GET /api/auth/kakao           |

---

### 📅 이벤트 / 기념일 관리

| ID     | 기능 설명              | 우선순위 | 관련 API                       |
|--------|----------------------|---------|--------------------------------|
| BE-101 | 이벤트 생성           | 높음    | POST /api/events               |
| BE-102 | 이벤트 목록 조회      | 높음    | GET /api/events                |
| BE-103 | 특정 이벤트 조회      | 높음    | GET /api/events/{eventId}      |
| BE-104 | 이벤트 수정           | 높음    | PUT /api/events/{eventId}      |
| BE-105 | 이벤트 삭제(비활성화) | 높음    | DELETE /api/events/{eventId}   |
| BE-106 | 다가오는 이벤트 조회  | 높음    | GET /api/events/upcoming       |
| BE-107 | 이벤트 추적 토글      | 높음    | PUT /api/events/{eventId}/tracking |
| BE-108 | 이벤트 타입별 필터링  | 중간    | GET /api/events?type={type}    |

---

### ⏰ 리마인더 시스템

| ID     | 기능 설명                    | 우선순위 | 관련 API                                |
|--------|----------------------------|---------|----------------------------------------|
| BE-201 | 리마인더 설정 수정           | 높음    | PUT /api/events/{eventId}/reminders    |
| BE-202 | 리마인더 발송 로그 조회      | 중간    | GET /api/reminders/logs                |
| BE-203 | 실패한 리마인더 재발송       | 중간    | POST /api/reminders/retry              |
| BE-204 | 즉시 리마인더 테스트 발송    | 낮음    | POST /api/reminders/immediate/{eventId}|

---

### 🎁 선물 관리

| ID     | 기능 설명                | 우선순위 | 관련 API                         |
|--------|------------------------|---------|----------------------------------|
| BE-301 | 선물 아이템 생성        | 높음    | POST /api/gifts                  |
| BE-302 | 선물 목록 조회          | 높음    | GET /api/gifts                   |
| BE-303 | 특정 이벤트 선물 조회   | 높음    | GET /api/gifts/event/{eventId}   |
| BE-304 | 선물 아이템 수정        | 높음    | PUT /api/gifts/{giftId}          |
| BE-305 | 선물 아이템 삭제        | 높음    | DELETE /api/gifts/{giftId}       |
| BE-306 | 선물 구매 완료 표시     | 높음    | PUT /api/gifts/{giftId}/purchase |
| BE-307 | 미구매 선물 조회        | 중간    | GET /api/gifts?purchased=false   |
| BE-308 | 카테고리별 선물 조회    | 중간    | GET /api/gifts?category={cat}    |

---

### 🤖 AI 기반 선물 추천

| ID     | 기능 설명                     | 우선순위 | 관련 API                              |
|--------|------------------------------|---------|---------------------------------------|
| BE-401 | AI 선물 추천 요청             | 중간    | POST /api/gifts/recommend             |
| BE-402 | 선호도 기반 추천              | 낮음    | POST /api/gifts/recommend/preference  |

---

### 📊 대시보드 및 통계

| ID     | 기능 설명                  | 우선순위 | 관련 API                        |
|--------|--------------------------|---------|----------------------------------|
| BE-501 | 대시보드 요약 정보         | 중간    | GET /api/dashboard               |
| BE-502 | 월별 이벤트 통계           | 중간    | GET /api/statistics/events       |
| BE-503 | 선물 구매 내역 통계        | 낮음    | GET /api/statistics/gifts        |
| BE-504 | 리마인더 발송 통계         | 낮음    | GET /api/statistics/reminders    |
| BE-505 | 캘린더 뷰 데이터 조회      | 중간    | GET /api/calendar                |

---

## 우선순위 정의

- **높음**: MVP(Minimum Viable Product)에 필수적인 기능
- **중간**: 사용성 향상을 위해 필요한 기능
- **낮음**: 향후 확장 시 추가할 기능

---

## 비기능 요구사항

### 성능
- API 응답 시간: 평균 500ms 이하
- 동시 접속자: 100명 이상 지원
- 리마인더 발송 정확도: 100% (누락 없음)

### 보안
- HTTPS 통신 필수
- JWT 토큰 기반 인증
- 비밀번호 암호화 (BCrypt)
- SQL Injection, XSS 방지

### 가용성
- 시스템 가동률: 99% 이상
- 일일 백업 수행
- 장애 복구 시간: 1시간 이내
