# 로컬 테스트 가이드

Day Memory 애플리케이션을 로컬 환경에서 실행하고 테스트하는 방법입니다.

## 📋 사전 요구사항

### 필수 소프트웨어
- **Java**: 17 이상
- **Node.js**: 18 이상
- **PostgreSQL**: 14 이상
- **Docker** (선택사항): Docker Compose 사용 시

### 설치 확인
```bash
java -version        # Java 17+
node -version        # Node 18+
npm -version         # npm 9+
psql --version       # PostgreSQL 14+
```

## 🗄️ 1. 데이터베이스 설정

### 방법 1: 로컬 PostgreSQL 사용

#### PostgreSQL 설치 및 시작
```bash
# macOS (Homebrew)
brew install postgresql@14
brew services start postgresql@14

# Linux (Ubuntu/Debian)
sudo apt update
sudo apt install postgresql-14
sudo systemctl start postgresql
```

#### 데이터베이스 생성
```bash
# PostgreSQL에 접속
psql postgres

# 데이터베이스 및 사용자 생성
CREATE DATABASE daymemory;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE daymemory TO postgres;

# 종료
\q
```

### 방법 2: Docker로 PostgreSQL 실행 (권장)

```bash
# PostgreSQL 컨테이너 실행
docker run --name day-memory-postgres \
  -e POSTGRES_DB=daymemory \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:14

# 실행 확인
docker ps | grep day-memory-postgres

# 로그 확인
docker logs day-memory-postgres
```

#### Docker Compose 사용 (전체 스택)

```bash
# 프로젝트 루트에서
docker-compose up -d postgres

# 모든 서비스 확인
docker-compose ps
```

## 🔧 2. 백엔드 설정

### 환경 변수 설정

프로젝트 루트 또는 backend 폴더에 `.env` 파일 생성:

```bash
# backend/.env 또는 프로젝트 루트/.env
DB_URL=jdbc:postgresql://localhost:5432/daymemory
DB_USERNAME=postgres
DB_PASSWORD=postgres

JWT_SECRET=your-secret-key-must-be-at-least-256-bits-long-for-HS256-algorithm
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

AI_API_KEY=your-openai-api-key
AI_PROVIDER=openai
AI_MODEL=gpt-3.5-turbo

SPRING_PROFILES_ACTIVE=dev
```

### 백엔드 실행

#### Gradle 사용
```bash
cd backend

# 의존성 설치 및 빌드
./gradlew clean build -x test

# 애플리케이션 실행
./gradlew bootRun

# 또는 빌드된 JAR 실행
java -jar build/libs/day-memory-0.0.1-SNAPSHOT.jar
```

#### IDE에서 실행
1. IntelliJ IDEA 또는 Eclipse에서 프로젝트 열기
2. `DayMemoryApplication.java` 찾기
3. `main` 메서드 실행 (▶️ 버튼)

### 백엔드 실행 확인

```bash
# 헬스 체크
curl http://localhost:8080/actuator/health

# 예상 응답:
# {"status":"UP"}

# API 문서 (Swagger) 확인
open http://localhost:8080/swagger-ui/index.html
```

## 💻 3. 프론트엔드 설정

### 환경 변수 설정

`frontend/.env.local` 파일 생성 (이미 생성됨):

```bash
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_NAME=Day Memory
VITE_APP_VERSION=1.0.0
```

### 의존성 설치

```bash
cd frontend

# npm 사용
npm install

# 또는 yarn 사용
yarn install
```

### 프론트엔드 실행

```bash
# 개발 서버 시작
npm run dev

# 예상 출력:
# VITE v7.x.x  ready in xxx ms
# ➜  Local:   http://localhost:5173/
# ➜  Network: use --host to expose
```

### 프론트엔드 실행 확인

브라우저에서 http://localhost:5173 접속

## 🔗 4. 연동 확인

### CORS 설정 확인

백엔드의 CORS 설정이 프론트엔드 URL을 허용하는지 확인:

```java
// backend/src/main/java/com/daymemory/config/CorsConfig.java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }
}
```

### API 연동 테스트

#### 1. 회원가입 테스트

브라우저에서:
1. http://localhost:5173 접속
2. "회원가입" 페이지로 이동
3. 테스트 계정 생성:
   - 이메일: test@example.com
   - 비밀번호: Test1234!
   - 이름: 테스트사용자

#### 2. 로그인 테스트

1. 생성한 계정으로 로그인
2. 대시보드 페이지로 리다이렉트 확인

#### 3. API 호출 확인

브라우저 개발자 도구 (F12) → Network 탭:
- API 요청: `http://localhost:8080/api/auth/login`
- 응답 상태: `200 OK`
- 응답 본문: `{ accessToken, refreshToken, user }`

### cURL로 직접 테스트

```bash
# 회원가입
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test1234!",
    "name": "테스트사용자"
  }'

# 로그인
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test1234!"
  }'

# 응답에서 accessToken 복사 후

# 내 정보 조회
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 🧪 5. 기능별 테스트 시나리오

### 이벤트 관리

1. **이벤트 생성**
   - 대시보드 → "새 이벤트" 버튼
   - 제목, 날짜, 타입, 받는 사람 입력
   - 리마인더 설정 (7일 전, 3일 전, 1일 전)
   - "저장" 클릭

2. **이벤트 목록 조회**
   - "이벤트" 메뉴 클릭
   - 필터링 (타입, 날짜 범위)
   - 정렬 (최신순, 날짜순)
   - 페이지네이션 확인

3. **이벤트 수정/삭제**
   - 이벤트 카드 클릭
   - 상세 페이지에서 "수정" 버튼
   - 정보 변경 후 저장
   - "삭제" 버튼으로 삭제

### 선물 관리

1. **선물 추가**
   - "선물" 메뉴 → "새 선물" 버튼
   - 선물명, 카테고리, 가격 입력
   - 이벤트 연결 (선택)
   - URL 입력 (선택)

2. **AI 추천 받기**
   - "AI 추천" 메뉴
   - 이벤트 선택
   - "추천 받기" 버튼
   - 추천 결과 확인
   - 선물 목록에 추가

### 리마인더

1. **리마인더 설정**
   - "리마인더" 메뉴
   - 글로벌 설정: 기본 알림 일자
   - 알림 방법 (이메일, SMS)
   - "저장" 버튼

2. **리마인더 로그**
   - "리마인더 로그" 탭
   - 발송 상태 확인 (성공/실패)
   - 실패한 알림 재발송

### 캘린더 뷰

1. **월간 캘린더**
   - "캘린더" 메뉴
   - 월 선택
   - 이벤트가 날짜별로 표시되는지 확인
   - 이벤트 클릭 시 팝업 확인

## 🎨 6. 프론트엔드 전용 테스트

### 다크 모드

1. 헤더 우측 상단 테마 토글 버튼 클릭
2. 라이트/다크 모드 전환 확인
3. 페이지 새로고침 후 테마 유지 확인

### 접근성

1. **키보드 네비게이션**
   - Tab 키로 요소 이동
   - Enter/Space로 버튼 활성화
   - Esc로 모달 닫기

2. **스크린 리더**
   - macOS: VoiceOver (Cmd+F5)
   - 모든 이미지에 대체 텍스트 확인
   - 폼 필드와 레이블 연결 확인

### PWA

1. **설치**
   - Chrome: 주소창 우측 설치 아이콘
   - "설치" 버튼 클릭
   - 앱 아이콘 확인

2. **오프라인**
   - 개발자 도구 → Network → Offline 체크
   - 페이지 새로고침
   - 캐시된 콘텐츠 표시 확인
   - 오프라인 알림 확인

## 🧪 7. 자동화 테스트 실행

### 백엔드 테스트

```bash
cd backend

# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests UserServiceTest

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

### 프론트엔드 테스트

```bash
cd frontend

# 유닛 테스트 실행
npm run test

# UI 모드로 테스트 실행
npm run test:ui

# 커버리지 리포트 생성
npm run test:coverage
open coverage/index.html
```

## 🐛 8. 트러블슈팅

### 백엔드가 시작되지 않음

**문제**: `Connection refused to localhost:5432`

**해결**:
```bash
# PostgreSQL 실행 확인
docker ps | grep postgres
# 또는
brew services list | grep postgresql

# 재시작
docker start day-memory-postgres
# 또는
brew services restart postgresql@14
```

### 프론트엔드에서 API 호출 실패

**문제**: `CORS policy: No 'Access-Control-Allow-Origin' header`

**해결**:
1. 백엔드 CorsConfig.java 확인
2. `allowedOrigins`에 `http://localhost:5173` 포함되었는지 확인
3. 백엔드 재시작

### JWT 토큰 만료

**문제**: `401 Unauthorized`

**해결**:
1. 로그아웃 후 재로그인
2. localStorage 확인:
   ```javascript
   // 브라우저 콘솔에서
   localStorage.clear()
   location.reload()
   ```

### 포트 충돌

**문제**: `Port 8080 is already in use`

**해결**:
```bash
# 포트 사용 프로세스 확인
lsof -i :8080

# 프로세스 종료
kill -9 <PID>

# 또는 다른 포트 사용
# backend/src/main/resources/application.yml
server:
  port: 8081
```

## 📊 9. 모니터링

### 백엔드 모니터링

```bash
# 헬스 체크
curl http://localhost:8080/actuator/health

# 애플리케이션 정보
curl http://localhost:8080/actuator/info

# 메트릭스
curl http://localhost:8080/actuator/metrics
```

### 로그 확인

```bash
# 백엔드 로그
tail -f backend/logs/day-memory.log

# Docker 로그
docker logs -f day-memory-backend
docker logs -f day-memory-postgres
```

## 🚀 10. 프로덕션 빌드 테스트

### 백엔드 빌드

```bash
cd backend

# JAR 빌드
./gradlew clean build

# 빌드된 JAR 실행
java -jar build/libs/day-memory-0.0.1-SNAPSHOT.jar
```

### 프론트엔드 빌드

```bash
cd frontend

# 프로덕션 빌드
npm run build

# 빌드 결과물 확인
ls -lh dist/

# 프리뷰 서버로 테스트
npm run preview

# 브라우저에서 http://localhost:4173 접속
```

## 📝 체크리스트

연동 테스트 완료 체크리스트:

- [ ] PostgreSQL 실행 및 데이터베이스 생성
- [ ] 백엔드 환경 변수 설정
- [ ] 백엔드 실행 (포트 8080)
- [ ] 백엔드 헬스 체크 성공
- [ ] 프론트엔드 환경 변수 설정
- [ ] 프론트엔드 실행 (포트 5173)
- [ ] 회원가입 성공
- [ ] 로그인 성공 및 토큰 발급
- [ ] 대시보드 접근
- [ ] 이벤트 CRUD 작동
- [ ] 선물 CRUD 작동
- [ ] AI 추천 작동
- [ ] 다크 모드 전환
- [ ] 오프라인 모드 확인
- [ ] 테스트 통과 (백엔드 + 프론트엔드)

## 🆘 추가 지원

문제가 해결되지 않을 경우:

1. **로그 확인**: 백엔드 및 브라우저 콘솔 로그
2. **GitHub Issues**: 프로젝트 저장소의 Issues 탭
3. **문서 참조**:
   - [DOCKER.md](../DOCKER.md)
   - [PERFORMANCE.md](PERFORMANCE.md)
   - [ACCESSIBILITY.md](ACCESSIBILITY.md)
   - [PWA.md](PWA.md)
