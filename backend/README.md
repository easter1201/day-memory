# Day Memory Backend

Spring Boot 기반 기념일 관리 백엔드 API

## 기술 스택

- Java 17
- Spring Boot 3.2.1
- Spring Data JPA
- PostgreSQL
- Spring Mail
- Lombok

## 데이터베이스 설정

PostgreSQL 데이터베이스를 생성해주세요:

```sql
CREATE DATABASE daymemory;
```

[application.yml](src/main/resources/application.yml)에서 데이터베이스 연결 정보를 수정하세요:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/daymemory
    username: your-username
    password: your-password
```

## 이메일 설정

이메일 리마인더를 사용하려면 [application.yml](src/main/resources/application.yml)에서 메일 설정을 구성하세요:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

Gmail을 사용하는 경우 앱 비밀번호를 생성해야 합니다.

## 실행 방법

### Maven Wrapper 사용 (Windows)

```bash
mvnw.cmd spring-boot:run
```

### Maven Wrapper 사용 (Mac/Linux)

```bash
./mvnw spring-boot:run
```

### Maven 직접 사용

```bash
mvn spring-boot:run
```

애플리케이션은 기본적으로 http://localhost:8080에서 실행됩니다.

## API 엔드포인트

### Events

- `GET /api/events?userId={userId}` - 사용자의 모든 이벤트 조회
- `GET /api/events/{eventId}` - 특정 이벤트 조회
- `POST /api/events?userId={userId}` - 새 이벤트 생성
- `PUT /api/events/{eventId}` - 이벤트 수정
- `DELETE /api/events/{eventId}` - 이벤트 삭제
- `GET /api/events/upcoming?userId={userId}&days={days}` - 다가오는 이벤트 조회

### Gift Items

- `GET /api/gifts?userId={userId}` - 사용자의 모든 선물 아이템 조회
- `GET /api/gifts/event/{eventId}` - 특정 이벤트의 선물 아이템 조회
- `POST /api/gifts?userId={userId}` - 새 선물 아이템 생성
- `PUT /api/gifts/{giftId}` - 선물 아이템 수정
- `PATCH /api/gifts/{giftId}/purchase` - 구매 상태 토글
- `DELETE /api/gifts/{giftId}` - 선물 아이템 삭제

## 리마인더 스케줄러

[ReminderService](src/main/java/com/daymemory/service/ReminderService.java)는 매일 오전 9시에 실행되어 다음 알림을 전송합니다:

- 30일 전 알림
- 7일 전 알림
- 1일 전 알림

각 이벤트의 리마인더 설정에 따라 알림이 전송됩니다.

## 주요 기능

1. **이벤트 관리**: 기념일, 생일, 명절 등 다양한 이벤트 등록 및 관리
2. **D-day 계산**: 자동으로 D-day 계산
3. **리마인더 알림**: 설정된 일정에 따라 이메일 알림 전송
4. **선물 관리**: 이벤트별 선물 아이디어 저장 및 구매 상태 관리
5. **반복 이벤트**: 매년 반복되는 이벤트 지원

## 프로젝트 구조

```
src/main/java/com/daymemory/
├── config/              # 설정 파일
├── controller/          # REST API 컨트롤러
├── domain/
│   ├── dto/            # DTO 클래스
│   ├── entity/         # JPA 엔티티
│   └── repository/     # JPA 레포지토리
├── service/            # 비즈니스 로직
├── exception/          # 예외 처리
└── util/               # 유틸리티
```
