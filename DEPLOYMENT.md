# Day Memory 배포 가이드

## 필수 환경 변수

배포하기 전에 다음 환경 변수들을 설정해야 합니다.

### 데이터베이스 설정
```
DB_NAME=daymemory
DB_USERNAME=postgres
DB_PASSWORD=<your-secure-password>
DB_PORT=5432
```

### JWT 설정
```
JWT_SECRET=<your-secret-key-must-be-at-least-256-bits-long>
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000
```

### 이메일 설정 (Gmail SMTP)
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<your-email@gmail.com>
MAIL_PASSWORD=<your-gmail-app-password>
```

**Gmail 앱 비밀번호 발급 방법:**
1. Google 계정 관리 → 보안
2. 2단계 인증 활성화
3. 앱 비밀번호 생성
4. 생성된 16자리 비밀번호를 `MAIL_PASSWORD`에 설정

### AI API 설정 (Google Gemini)
```
AI_PROVIDER=gemini
AI_API_KEY=<your-gemini-api-key>
AI_MODEL=gemini-2.5-flash
```

**Gemini API 키 발급:**
1. https://ai.google.dev/ 접속
2. API 키 생성
3. 무료 티어: 분당 15 요청, 일일 1500 요청

### Naver Shopping API 설정
```
NAVER_CLIENT_ID=<your-naver-client-id>
NAVER_CLIENT_SECRET=<your-naver-client-secret>
```

**Naver API 발급:**
1. https://developers.naver.com/apps 접속
2. 애플리케이션 등록
3. 검색 API - 쇼핑 선택

### OAuth 설정 (선택사항)
```
GOOGLE_CLIENT_ID=<your-google-oauth-client-id>
GOOGLE_CLIENT_SECRET=<your-google-oauth-client-secret>
KAKAO_CLIENT_ID=<your-kakao-client-id>
KAKAO_CLIENT_SECRET=<your-kakao-client-secret>
```

### 서버 설정
```
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

## Docker Compose로 배포

### 1. 환경 변수 파일 생성

루트 디렉토리에 `.env` 파일을 생성하고 위의 환경 변수들을 설정합니다.

```bash
cp .env.example .env
# .env 파일을 편집하여 실제 값으로 변경
```

### 2. Docker Compose 실행

```bash
# 프로덕션 모드로 실행
docker compose up -d

# 로그 확인
docker compose logs -f backend

# 상태 확인
docker compose ps
```

### 3. 헬스 체크

```bash
curl http://localhost:8080/actuator/health
```

정상 응답:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "mail": {"status": "UP"}
  }
}
```

## 프론트엔드 배포

### 빌드

```bash
cd frontend
npm run build
```

빌드된 파일은 `frontend/dist` 디렉토리에 생성됩니다.

### Nginx 설정 예시

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 프론트엔드
    location / {
        root /path/to/frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    # 백엔드 API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## AWS 배포 (예시)

### EC2 인스턴스

1. **인스턴스 생성**
   - Ubuntu 22.04 LTS
   - t3.medium 이상 권장
   - 보안 그룹: 80, 443, 8080, 5432 포트 개방

2. **Docker 설치**
```bash
sudo apt update
sudo apt install -y docker.io docker-compose
sudo usermod -aG docker $USER
```

3. **코드 배포**
```bash
git clone <repository-url>
cd day-memory
cp .env.example .env
# .env 편집
docker compose up -d
```

### RDS 사용 (권장)

`.env` 파일에서 DB 설정 변경:
```
DB_URL=jdbc:postgresql://<rds-endpoint>:5432/daymemory
DB_USERNAME=<rds-username>
DB_PASSWORD=<rds-password>
```

## 보안 주의사항

1. **환경 변수 파일 보호**
   - `.env` 파일은 절대 Git에 커밋하지 마세요
   - 프로덕션 서버에서 파일 권한: `chmod 600 .env`

2. **JWT Secret**
   - 최소 256비트 이상의 무작위 문자열 사용
   - 주기적으로 변경

3. **데이터베이스**
   - 강력한 비밀번호 사용
   - 외부 접근 차단 (백엔드에서만 접근)

4. **HTTPS 사용**
   - Let's Encrypt 무료 SSL 인증서 사용 권장
   - Nginx 또는 Traefik으로 HTTPS 리버스 프록시 설정

## 모니터링

### 로그 확인
```bash
# 백엔드 로그
docker compose logs -f backend

# 특정 시간대 로그
docker compose logs --since 1h backend

# 에러 로그만
docker compose logs backend 2>&1 | grep ERROR
```

### 헬스 체크 자동화
```bash
# cron 설정 (5분마다 헬스 체크)
*/5 * * * * curl -f http://localhost:8080/actuator/health || echo "Backend is down!" | mail -s "Alert" admin@example.com
```

## 백업

### 데이터베이스 백업
```bash
# 백업 생성
docker exec daymemory-postgres pg_dump -U postgres daymemory > backup_$(date +%Y%m%d).sql

# 복원
docker exec -i daymemory-postgres psql -U postgres daymemory < backup_20251117.sql
```

## 트러블슈팅

### 백엔드가 시작하지 않을 때
1. 로그 확인: `docker compose logs backend`
2. 환경 변수 확인: `docker exec daymemory-backend env`
3. 데이터베이스 연결 확인

### 메일 발송 실패
1. Gmail 앱 비밀번호 재확인
2. 2단계 인증 활성화 확인
3. 로그에서 상세 오류 확인

### AI 추천 실패
1. Gemini API 키 확인
2. API 할당량 확인
3. 로그에서 Gemini API 응답 확인

## 업데이트

```bash
# 최신 코드 가져오기
git pull

# Docker 이미지 재빌드 및 재시작
docker compose up -d --build

# 변경사항이 적용되지 않으면 완전히 재생성
docker compose down
docker compose up -d --build
```
