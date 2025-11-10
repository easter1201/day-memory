# Day Memory

기념일/이벤트 관리 및 선물 추천 애플리케이션

## 주요 기능

- 기념일/이벤트 등록 및 관리
- D-day 자동 계산
- 이메일 리마인드 알림 (30일, 1주일, 1일 전)
- 사용자 맞춤 선물 리스트 관리
- AI 기반 선물 추천

## 기술 스택

### Backend
- Java 17
- Spring Boot
- PostgreSQL
- Spring Data JPA
- Spring Mail (이메일 알림)

### Frontend
- React 18
- TypeScript
- Redux Toolkit
- React Router
- Axios

## 프로젝트 구조

```
day-memory/
├── backend/          # Spring Boot 애플리케이션
├── frontend/         # React 애플리케이션
└── README.md
```

## 시작하기

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start
```

## 개발 환경

- Java 17+
- Node.js 18+
- PostgreSQL 14+
