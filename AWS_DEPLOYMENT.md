# AWS 배포 가이드

## 🚀 프로덕션 배포 체크리스트

### 1. 환경 변수 설정

AWS EC2 또는 ECS에 배포 시, 다음 환경 변수를 설정하세요:

```bash
# .env.aws 파일을 참고하여 다음 값들을 설정합니다:

# 필수 설정 (반드시 변경)
export CORS_ALLOWED_ORIGINS="http://your-domain.com"
export FRONTEND_API_BASE_URL="http://your-domain.com/api"
export JWT_SECRET="your-secure-secret-key-min-256-bits"
export DB_PASSWORD="your-secure-database-password"
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
export AI_API_KEY="your-gemini-api-key"
```

### 2. 프론트엔드 API 엔드포인트 설정

프로덕션 배포 시, 프론트엔드가 올바른 백엔드 URL로 요청하도록 설정해야 합니다.

#### 방법 1: Docker-compose 사용 (권장)

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d \
  -e CORS_ALLOWED_ORIGINS="https://your-domain.com" \
  -e FRONTEND_API_BASE_URL="https://your-domain.com/api"
```

#### 방법 2: 환경 변수 직접 설정

```bash
export CORS_ALLOWED_ORIGINS="https://your-domain.com"
export FRONTEND_API_BASE_URL="https://your-domain.com/api"
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

#### 방법 3: .env 파일 사용

```bash
cp .env.aws .env
# .env 파일을 편집하여 필요한 값 입력
nano .env

docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 3. AWS ALB (Application Load Balancer) 설정

AWS ALB를 사용하는 경우:

```bash
# ALB의 DNS 이름을 CORS_ALLOWED_ORIGINS에 설정
export CORS_ALLOWED_ORIGINS="http://your-alb-dns.elb.amazonaws.com"
export FRONTEND_API_BASE_URL="http://your-alb-dns.elb.amazonaws.com/api"
```

### 4. 도메인 설정 (Route 53)

Custom domain 사용 시:

```bash
export CORS_ALLOWED_ORIGINS="https://api.yourdomain.com"
export FRONTEND_API_BASE_URL="https://api.yourdomain.com/api"
```

### 5. Docker-compose 배포 명령어

#### 초기 배포

```bash
# 환경 변수 설정
source .env.aws

# 프로덕션 환경으로 배포
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

#### 배포 상태 확인

```bash
# 컨테이너 상태 확인
docker-compose -f docker-compose.yml -f docker-compose.prod.yml ps

# 로그 확인
docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f backend
docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f frontend
```

#### 배포 업데이트

```bash
# 새로운 코드 배포
docker-compose -f docker-compose.yml -f docker-compose.prod.yml pull
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

#### 배포 중지

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml down
```

### 6. 보안 설정

#### HTTPS 설정 (AWS ACM + ALB)

ALB에서 HTTPS를 설정하고, 다음과 같이 설정합니다:

```bash
# Docker-compose에서는 HTTP로 통신하고, ALB에서 HTTPS로 변환
export CORS_ALLOWED_ORIGINS="https://yourdomain.com"
export FRONTEND_API_BASE_URL="https://yourdomain.com/api"

# 백엔드 내부 통신은 여전히 HTTP
# CORS 설정에서는 외부 도메인(HTTPS)으로 설정
```

#### Nginx SSL 설정 (선택사항)

Nginx를 사용하는 경우, docker-compose.prod.yml에 설정:

```yaml
  nginx:
    volumes:
      - ./nginx/ssl:/etc/nginx/ssl:ro
    # SSL 인증서 경로: ./nginx/ssl/cert.pem, ./nginx/ssl/key.pem
```

### 7. 데이터베이스 보안

```bash
# PostgreSQL 패스워드는 AWS Secrets Manager에서 관리하는 것을 권장
export DB_PASSWORD=$(aws secretsmanager get-secret-value \
  --secret-id daymemory-db-password \
  --query SecretString --output text)
```

### 8. 헬스 체크 확인

배포 후 헬스 체크:

```bash
# 백엔드 헬스 체크
curl http://localhost:8080/actuator/health

# 프론트엔드 헬스 체크
curl http://localhost/health

# AWS ALB를 통한 헬스 체크
curl http://your-alb-dns.elb.amazonaws.com/api/actuator/health
```

### 9. 문제 해결

#### 프론트엔드가 백엔드에 연결할 수 없음

```bash
# CORS 설정 확인
echo $CORS_ALLOWED_ORIGINS
echo $FRONTEND_API_BASE_URL

# 로그 확인
docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f backend | grep CORS
docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f frontend
```

#### 데이터베이스 연결 오류

```bash
# PostgreSQL 연결 확인
docker-compose -f docker-compose.yml -f docker-compose.prod.yml exec backend \
  psql -h postgres -U postgres -d daymemory_prod -c "SELECT 1"

# 로그 확인
docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f postgres
```

#### 메모리 부족 오류

```bash
# Docker 리소스 확인
docker stats

# 배포 시 메모리 제한 조정 (docker-compose.prod.yml에서 설정)
deploy:
  resources:
    limits:
      memory: 2048M  # 필요에 따라 증가
```

### 10. 모니터링 (AWS CloudWatch)

```bash
# CloudWatch에 로그 전송
# docker-compose.prod.yml에서 awslogs 드라이버 설정

logging:
  driver: awslogs
  options:
    awslogs-group: /aws/daymemory
    awslogs-region: ap-northeast-2
    awslogs-stream-prefix: backend
```

## 📝 주요 변경사항

### 백엔드
- **CORS 설정** 환경 변수화 (`CORS_ALLOWED_ORIGINS`)
- **Application.yml** cors 설정 추가
- **CorsConfig.java** 환경변수 기반 동적 설정

### 프론트엔드
- **Dockerfile** 추가 (프로덕션 빌드)
- **nginx.conf** 추가 (SPA 라우팅 설정)
- 기존 **API 엔드포인트** 환경변수 유지 (`VITE_API_BASE_URL`)

### Docker-compose
- **docker-compose.yml**: `CORS_ALLOWED_ORIGINS` 환경변수 추가
- **docker-compose.prod.yml**: 
  - 프론트엔드 서비스 추가
  - 백엔드 `CORS_ALLOWED_ORIGINS` 환경변수 추가
  - 리소스 제한 설정

## 🔗 환경별 설정 예시

### 로컬 개발 환경
```bash
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
FRONTEND_API_BASE_URL=http://localhost:8080/api
```

### AWS EC2 (IP 주소)
```bash
CORS_ALLOWED_ORIGINS=http://ec2-public-ip:3000
FRONTEND_API_BASE_URL=http://ec2-public-ip:8080/api
```

### AWS ALB (DNS)
```bash
CORS_ALLOWED_ORIGINS=http://alb-dns.elb.amazonaws.com
FRONTEND_API_BASE_URL=http://alb-dns.elb.amazonaws.com/api
```

### 커스텀 도메인 (HTTPS)
```bash
CORS_ALLOWED_ORIGINS=https://yourdomain.com
FRONTEND_API_BASE_URL=https://yourdomain.com/api
```

---

**문제 발생 시**: 로그를 확인하여 `ERR_CONNECTION_REFUSED` 오류의 정확한 원인을 파악하세요.
