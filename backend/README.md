# Day Memory Backend

Spring Boot 기반 기념일 관리 백엔드 API

## 기술 스택

- Java 17
- Spring Boot 3.2.1
- Spring Data JPA
- PostgreSQL
- Spring Mail
- Lombok

## 환경 설정

### 1. 환경 변수 설정

`.env.example` 파일을 복사하여 `.env` 파일을 생성하고 실제 값으로 변경하세요:

```bash
cp .env.example .env
```

필수 환경 변수:
- `DB_PASSWORD`: 데이터베이스 비밀번호
- `JWT_SECRET`: JWT 토큰 암호화 키 (최소 256비트)
- `MAIL_USERNAME`: 이메일 발송 계정
- `MAIL_PASSWORD`: Gmail App Password
- `AI_API_KEY`: OpenAI API 키
- `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`: Google OAuth
- `KAKAO_CLIENT_ID`, `KAKAO_CLIENT_SECRET`: Kakao OAuth

### 2. 프로파일 설정

애플리케이션은 환경별 프로파일을 지원합니다:

#### 개발 환경 (dev) - 기본값
```bash
mvn spring-boot:run
# 또는
export SPRING_PROFILES_ACTIVE=dev
```

#### 운영 환경 (prod)
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 3. 데이터베이스 설정

PostgreSQL 데이터베이스를 생성해주세요:

```sql
CREATE DATABASE daymemory;
```

## 실행 방법

### Gradle Wrapper 사용 (Windows)

```bash
gradlew.bat bootRun
```

### Gradle Wrapper 사용 (Mac/Linux)

```bash
./gradlew bootRun
```

### Gradle 직접 사용

```bash
gradle bootRun
```

애플리케이션은 기본적으로 http://localhost:8080에서 실행됩니다.

## 빌드 방법

### JAR 파일 생성

```bash
./gradlew bootJar
```

생성된 JAR 파일은 `build/libs/backend.jar`에 위치합니다.

### 테스트 실행

```bash
./gradlew test
```

### 빌드 및 테스트

```bash
./gradlew build
```

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
