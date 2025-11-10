# 데이터베이스 스키마 설계

## 개요

Day Memory 프로젝트의 PostgreSQL 데이터베이스 스키마를 정의한 문서입니다.

---

## ERD 다이어그램

```
users (1) ──< (N) events (1) ──< (N) event_reminders
                │
                └──< (N) reminder_logs

users (1) ──< (N) gift_items (N) >── (1) events
```

---

## 테이블 상세 설계

### 테이블명: users

**설명**: 사용자 정보를 저장하는 테이블

| 컬럼명      | 타입         | 제약조건                  | 설명           |
|------------|-------------|--------------------------|---------------|
| id         | BIGINT      | PK, AUTO_INCREMENT       | 사용자 ID      |
| email      | VARCHAR(255)| UNIQUE, NOT NULL         | 이메일         |
| password   | VARCHAR(255)| NOT NULL                 | 비밀번호 (암호화)|
| name       | VARCHAR(100)| NOT NULL                 | 이름           |
| created_at | TIMESTAMP   | NOT NULL                 | 생성 일시      |
| updated_at | TIMESTAMP   | NOT NULL                 | 수정 일시      |

**인덱스**:
- `idx_users_email`: (email) UNIQUE

**관계**:
- events.user_id → users.id (1:N)
- gift_items.user_id → users.id (1:N)

---

### 테이블명: events

**설명**: 이벤트/기념일 정보를 저장하는 테이블

| 컬럼명         | 타입         | 제약조건                  | 설명                    |
|---------------|-------------|--------------------------|------------------------|
| id            | BIGINT      | PK, AUTO_INCREMENT       | 이벤트 ID               |
| user_id       | BIGINT      | FK, NOT NULL             | 사용자 ID (users.id)    |
| title         | VARCHAR(255)| NOT NULL                 | 이벤트 제목             |
| description   | TEXT        |                          | 설명                    |
| event_date    | DATE        | NOT NULL                 | 이벤트 날짜             |
| event_type    | VARCHAR(50) | NOT NULL                 | 이벤트 타입 (enum)      |
| is_recurring  | BOOLEAN     | NOT NULL, DEFAULT false  | 반복 여부               |
| is_active     | BOOLEAN     | NOT NULL, DEFAULT true   | 활성 여부               |
| is_tracking   | BOOLEAN     | NOT NULL, DEFAULT true   | 추적 여부               |
| created_at    | TIMESTAMP   | NOT NULL                 | 생성 일시               |
| updated_at    | TIMESTAMP   | NOT NULL                 | 수정 일시               |

**event_type enum 값**:
- `BIRTHDAY` - 생일
- `ANNIVERSARY_100` - 100일 기념일
- `ANNIVERSARY_200` - 200일 기념일
- `ANNIVERSARY_300` - 300일 기념일
- `ANNIVERSARY_1YEAR` - 1주년
- `ANNIVERSARY_CUSTOM` - 커스텀 기념일
- `DIARY_DAY` - 다이어리데이 (1/14)
- `VALENTINES_DAY` - 발렌타인데이 (2/14)
- `WHITE_DAY` - 화이트데이 (3/14)
- `BLACK_DAY` - 블랙데이 (4/14)
- `ROSE_DAY` - 로즈데이 (5/14)
- `KISS_DAY` - 키스데이 (6/14)
- `SILVER_DAY` - 실버데이 (7/14)
- `GREEN_DAY` - 그린데이 (8/14)
- `MUSIC_DAY` - 뮤직데이 (9/14)
- `WINE_DAY` - 와인데이 (10/14)
- `MOVIE_DAY` - 무비데이 (11/14)
- `HUG_DAY` - 허그데이 (12/14)
- `PEPERO_DAY` - 빼빼로데이 (11/11)
- `CHRISTMAS_EVE` - 크리스마스 이브
- `CHRISTMAS` - 크리스마스
- `NEW_YEAR_EVE` - 새해 전날
- `NEW_YEAR` - 새해
- `HOLIDAY` - 공휴일
- `VACATION` - 휴가
- `CUSTOM` - 사용자 정의

**인덱스**:
- `idx_events_user_id`: (user_id)
- `idx_events_event_date`: (event_date)
- `idx_events_user_active`: (user_id, is_active)

**관계**:
- user_id → users.id (N:1)
- event_reminders.event_id → events.id (1:N)
- reminder_logs.event_id → events.id (1:N)
- gift_items.event_id → events.id (1:N)

---

### 테이블명: event_reminders

**설명**: 이벤트별 리마인더 설정을 저장하는 테이블

| 컬럼명            | 타입    | 제약조건                  | 설명                        |
|------------------|--------|--------------------------|----------------------------|
| id               | BIGINT | PK, AUTO_INCREMENT       | 리마인더 ID                 |
| event_id         | BIGINT | FK, NOT NULL             | 이벤트 ID (events.id)       |
| days_before_event| INT    | NOT NULL                 | 이벤트 며칠 전 (예: 30, 7, 1)|
| is_active        | BOOLEAN| NOT NULL, DEFAULT true   | 활성 여부                   |
| created_at       | TIMESTAMP | NOT NULL              | 생성 일시                   |
| updated_at       | TIMESTAMP | NOT NULL              | 수정 일시                   |

**인덱스**:
- `idx_event_reminders_event_id`: (event_id)
- `idx_event_reminders_days`: (days_before_event)

**관계**:
- event_id → events.id (N:1)

---

### 테이블명: reminder_logs

**설명**: 리마인더 발송 기록을 저장하는 테이블

| 컬럼명            | 타입      | 제약조건                  | 설명                    |
|------------------|----------|--------------------------|------------------------|
| id               | BIGINT   | PK, AUTO_INCREMENT       | 로그 ID                 |
| event_id         | BIGINT   | FK, NOT NULL             | 이벤트 ID (events.id)   |
| days_before_event| INT      | NOT NULL                 | 발송 시점 (며칠 전)      |
| sent_at          | TIMESTAMP| NOT NULL                 | 발송 일시               |
| status           | VARCHAR(20)| NOT NULL               | 발송 상태 (SENT, FAILED)|
| created_at       | TIMESTAMP| NOT NULL                 | 생성 일시               |
| updated_at       | TIMESTAMP| NOT NULL                 | 수정 일시               |

**인덱스**:
- `idx_event_days_sent`: (event_id, days_before_event, sent_at)
- `idx_reminder_logs_sent_at`: (sent_at)

**관계**:
- event_id → events.id (N:1)

---

### 테이블명: gift_items

**설명**: 선물 아이템 정보를 저장하는 테이블

| 컬럼명         | 타입         | 제약조건                  | 설명                    |
|---------------|-------------|--------------------------|------------------------|
| id            | BIGINT      | PK, AUTO_INCREMENT       | 선물 ID                 |
| user_id       | BIGINT      | FK, NOT NULL             | 사용자 ID (users.id)    |
| event_id      | BIGINT      | FK, NULL                 | 연결된 이벤트 ID        |
| name          | VARCHAR(255)| NOT NULL                 | 선물 이름               |
| description   | TEXT        |                          | 설명                    |
| price         | INT         |                          | 가격                    |
| url           | VARCHAR(500)|                          | 구매 링크               |
| category      | VARCHAR(50) | NOT NULL                 | 카테고리 (enum)         |
| is_purchased  | BOOLEAN     | NOT NULL, DEFAULT false  | 구매 여부               |
| created_at    | TIMESTAMP   | NOT NULL                 | 생성 일시               |
| updated_at    | TIMESTAMP   | NOT NULL                 | 수정 일시               |

**category enum 값**:
- `FLOWER` - 꽃
- `JEWELRY` - 주얼리
- `COSMETICS` - 화장품
- `FASHION` - 패션
- `ELECTRONICS` - 전자기기
- `FOOD` - 음식/디저트
- `EXPERIENCE` - 체험/이벤트
- `BOOK` - 책
- `HOBBY` - 취미용품
- `OTHER` - 기타

**인덱스**:
- `idx_gift_items_user_id`: (user_id)
- `idx_gift_items_event_id`: (event_id)
- `idx_gift_items_category`: (category)

**관계**:
- user_id → users.id (N:1)
- event_id → events.id (N:1, nullable)

---

## 관계 요약

### 1:N 관계

1. **User → Events**
   - 한 사용자는 여러 이벤트를 가질 수 있음
   - `events.user_id` → `users.id`

2. **User → GiftItems**
   - 한 사용자는 여러 선물 아이템을 가질 수 있음
   - `gift_items.user_id` → `users.id`

3. **Event → EventReminders**
   - 한 이벤트는 여러 리마인더 설정을 가질 수 있음
   - `event_reminders.event_id` → `events.id`
   - Cascade: ALL, orphanRemoval: true

4. **Event → ReminderLogs**
   - 한 이벤트는 여러 리마인더 발송 기록을 가질 수 있음
   - `reminder_logs.event_id` → `events.id`

### N:1 관계 (Optional)

1. **GiftItems → Events**
   - 선물 아이템은 특정 이벤트와 연결될 수 있음 (선택사항)
   - `gift_items.event_id` → `events.id` (NULL 가능)

---

## DDL (Database Definition Language)

```sql
-- users 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);

-- events 테이블
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    is_recurring BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_tracking BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_events_user_id ON events(user_id);
CREATE INDEX idx_events_event_date ON events(event_date);
CREATE INDEX idx_events_user_active ON events(user_id, is_active);

-- event_reminders 테이블
CREATE TABLE event_reminders (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    days_before_event INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_event_reminders_event_id ON event_reminders(event_id);
CREATE INDEX idx_event_reminders_days ON event_reminders(days_before_event);

-- reminder_logs 테이블
CREATE TABLE reminder_logs (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    days_before_event INT NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_event_days_sent ON reminder_logs(event_id, days_before_event, sent_at);
CREATE INDEX idx_reminder_logs_sent_at ON reminder_logs(sent_at);

-- gift_items 테이블
CREATE TABLE gift_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_id BIGINT REFERENCES events(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price INT,
    url VARCHAR(500),
    category VARCHAR(50) NOT NULL,
    is_purchased BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_gift_items_user_id ON gift_items(user_id);
CREATE INDEX idx_gift_items_event_id ON gift_items(event_id);
CREATE INDEX idx_gift_items_category ON gift_items(category);
```
