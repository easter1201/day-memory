# Docker 배포 가이드

Day Memory 애플리케이션의 Docker 컨테이너화 및 배포 가이드입니다.

## 목차

- [사전 요구사항](#사전-요구사항)
- [빠른 시작](#빠른-시작)
- [개발 환경 설정](#개발-환경-설정)
- [운영 환경 설정](#운영-환경-설정)
- [Docker 명령어](#docker-명령어)
- [트러블슈팅](#트러블슈팅)

## 사전 요구사항

- Docker 20.10 이상
- Docker Compose 2.0 이상

### Docker 설치

**Mac:**
```bash
brew install --cask docker
```

**Linux:**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
```

**Windows:**
- [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop) 다운로드 및 설치

## 빠른 시작

### 1. 환경 변수 설정

```bash
# .env.docker.example을 복사하여 .env 파일 생성
cp .env.docker.example .env

# .env 파일을 편집하여 실제 값 입력
vim .env  # 또는 nano .env
```

### 2. 개발 환경 실행

```bash
# 모든 서비스 시작 (백엔드 + PostgreSQL + pgAdmin)
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# 로그 확인
docker-compose logs -f backend
```

### 3. 접속 확인

- **백엔드 API**: http://localhost:8080
- **API 문서 (Swagger)**: http://localhost:8080/swagger-ui.html
- **헬스체크**: http://localhost:8080/actuator/health
- **pgAdmin** (개발 환경): http://localhost:5050

## 개발 환경 설정

### 서비스 시작

```bash
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

### 개발 환경 특징

- **Hot Reload**: 소스 코드 변경 시 자동 재시작 (Spring Boot DevTools 필요)
- **pgAdmin**: 데이터베이스 관리 도구 포함 (http://localhost:5050)
- **포트 노출**: PostgreSQL 5432, Backend 8080 포트 외부 접근 가능
- **볼륨 마운트**: 소스 코드, Maven 캐시 마운트로 빠른 빌드

### pgAdmin 설정

1. http://localhost:5050 접속
2. 로그인 (기본값: admin@daymemory.com / admin)
3. 서버 추가:
   - Host: `postgres`
   - Port: `5432`
   - Database: `daymemory_dev`
   - Username: `.env` 파일의 `DB_USERNAME`
   - Password: `.env` 파일의 `DB_PASSWORD`

## 운영 환경 설정

### 서비스 시작

```bash
# 운영 환경으로 시작
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# 로그 확인
docker-compose logs -f
```

### 운영 환경 특징

- **프로덕션 프로파일**: Spring Boot prod 프로파일 사용
- **보안 강화**: PostgreSQL 포트 외부 접근 차단 (127.0.0.1만 허용)
- **성능 최적화**:
  - PostgreSQL 파라미터 튜닝
  - JVM 메모리 설정 (Xms512m, Xmx1024m)
  - G1GC 가비지 컬렉터 사용
- **리소스 제한**: CPU 및 메모리 제한 설정
- **로그 관리**: 로그 파일 크기 및 개수 제한 (10MB × 3개)
- **Nginx**: 리버스 프록시 (선택사항)

### 환경 변수 설정 (운영)

운영 환경에서는 `.env` 파일보다 환경 변수를 직접 설정하는 것을 권장합니다:

```bash
export DB_PASSWORD="secure_password"
export JWT_SECRET="your-256-bit-secret-key"
export MAIL_PASSWORD="your-app-password"
# ... 기타 환경 변수
```

## Docker 명령어

### 기본 명령어

```bash
# 서비스 시작 (foreground)
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up

# 서비스 시작 (background)
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# 서비스 중지
docker-compose down

# 서비스 중지 및 볼륨 삭제 (데이터베이스 초기화)
docker-compose down -v

# 서비스 재시작
docker-compose restart

# 특정 서비스만 재시작
docker-compose restart backend
```

### 로그 확인

```bash
# 모든 서비스 로그
docker-compose logs

# 특정 서비스 로그
docker-compose logs backend
docker-compose logs postgres

# 실시간 로그 (tail -f)
docker-compose logs -f backend

# 최근 100줄만 보기
docker-compose logs --tail=100 backend
```

### 컨테이너 관리

```bash
# 실행 중인 컨테이너 목록
docker-compose ps

# 컨테이너 내부 접속
docker-compose exec backend sh
docker-compose exec postgres psql -U postgres -d daymemory

# 리소스 사용량 확인
docker stats
```

### 빌드 및 이미지 관리

```bash
# 이미지 다시 빌드
docker-compose build

# 캐시 없이 빌드
docker-compose build --no-cache

# 이미지 목록
docker images

# 사용하지 않는 이미지 삭제
docker image prune

# 모든 정지된 컨테이너, 네트워크, 이미지 삭제
docker system prune -a
```

### 데이터베이스 관리

```bash
# PostgreSQL 접속
docker-compose exec postgres psql -U postgres -d daymemory_dev

# 데이터베이스 백업
docker-compose exec postgres pg_dump -U postgres daymemory_dev > backup.sql

# 데이터베이스 복원
cat backup.sql | docker-compose exec -T postgres psql -U postgres -d daymemory_dev

# 데이터베이스 초기화 (주의!)
docker-compose down -v
docker-compose up -d
```

## 트러블슈팅

### 포트 충돌

**문제**: `bind: address already in use`

**해결**:
```bash
# 포트 사용 중인 프로세스 확인 (Mac/Linux)
lsof -i :8080
lsof -i :5432

# 프로세스 종료
kill -9 <PID>

# 또는 .env 파일에서 포트 변경
SERVER_PORT=8081
DB_PORT=5433
```

### 컨테이너가 시작되지 않음

**문제**: 컨테이너가 계속 재시작되거나 종료됨

**해결**:
```bash
# 로그 확인
docker-compose logs backend

# 컨테이너 상태 확인
docker-compose ps

# 헬스체크 상태 확인
docker inspect daymemory-backend | grep -A 10 Health
```

### 데이터베이스 연결 실패

**문제**: `Connection refused` 또는 `could not connect to server`

**해결**:
```bash
# PostgreSQL 컨테이너 상태 확인
docker-compose ps postgres

# PostgreSQL 로그 확인
docker-compose logs postgres

# PostgreSQL 헬스체크
docker-compose exec postgres pg_isready -U postgres

# 네트워크 확인
docker network ls
docker network inspect daymemory_daymemory-network
```

### 빌드 실패

**문제**: Gradle 빌드 중 오류 발생

**해결**:
```bash
# 캐시 없이 다시 빌드
docker-compose build --no-cache backend

# 로컬에서 빌드 테스트
cd backend
./gradlew clean build -x test

# Docker 빌드 로그 상세 확인
docker-compose build --progress=plain backend

# Gradle 캐시 정리
./gradlew clean
rm -rf .gradle build
```

### 볼륨 권한 문제

**문제**: `Permission denied`

**해결**:
```bash
# 볼륨 권한 확인
docker-compose exec backend ls -la /app/uploads

# 권한 변경 (주의: 보안 고려)
docker-compose exec --user root backend chown -R spring:spring /app/uploads
```

### 메모리 부족

**문제**: `OutOfMemoryError` 또는 컨테이너 종료

**해결**:
```bash
# docker-compose.prod.yml에서 메모리 제한 증가
deploy:
  resources:
    limits:
      memory: 2048M  # 1536M에서 2048M로 증가

# JVM 힙 크기 조정
environment:
  JAVA_OPTS: -Xms512m -Xmx2048m  # 1024m에서 2048m로 증가
```

## 성능 최적화

### 멀티스테이지 빌드

Dockerfile은 멀티스테이지 빌드를 사용하여 이미지 크기를 최적화합니다:

- **Build Stage**: Gradle로 JAR 빌드
- **Runtime Stage**: JRE만 포함하여 경량화 (약 200MB)

### Layer Caching

의존성 다운로드를 별도 레이어로 분리하여 빌드 속도를 향상:

```dockerfile
# build.gradle.kts만 먼저 복사하여 의존성 다운로드 (캐시됨)
COPY build.gradle.kts .
COPY settings.gradle.kts .
RUN ./gradlew dependencies --no-daemon

# 소스 코드 변경 시에만 재빌드
COPY src ./src
RUN ./gradlew bootJar --no-daemon
```

## 보안 권장사항

1. **비root 사용자 실행**: 컨테이너는 `spring` 사용자로 실행
2. **환경 변수 보안**: `.env` 파일을 `.gitignore`에 추가
3. **포트 제한**: 운영 환경에서는 PostgreSQL 포트 외부 노출 금지
4. **시크릿 관리**: Docker Secrets 또는 외부 Vault 사용 권장
5. **이미지 스캔**: 정기적으로 취약점 스캔 (`docker scan`)

## 참고 자료

- [Docker 공식 문서](https://docs.docker.com/)
- [Docker Compose 문서](https://docs.docker.com/compose/)
- [Spring Boot Docker 가이드](https://spring.io/guides/topicals/spring-boot-docker/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
