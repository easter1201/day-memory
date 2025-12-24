# AWS 무료 티어로 Day Memory 배포하기

## 💰 비용 최소화 전략

### 무료 티어 한도 (12개월)
- **EC2**: t2.micro 인스턴스 750시간/월 (항상 실행 가능)
- **RDS**: db.t3.micro 인스턴스 750시간/월, 20GB 스토리지
- **S3**: 5GB 스토리지, 20,000 GET, 2,000 PUT 요청
- **CloudFront**: 50GB 데이터 전송 (12개월 무료)
- **Elastic IP**: 인스턴스에 연결되어 있으면 무료

### 예상 비용 (12개월 후)
- EC2 t2.micro: ~$8.50/월
- RDS db.t3.micro: ~$12/월
- **총 월 비용: ~$20** (최소 구성)

---

## 📋 1단계: AWS 계정 준비

### AWS 계정 생성
1. https://aws.amazon.com/ko/free/ 접속
2. "무료 계정 만들기" 클릭
3. 이메일, 비밀번호 설정
4. 신용카드 등록 (무료 티어 초과 시에만 과금)

### 리전 선택
**서울 리전 (ap-northeast-2) 권장**
- 가장 낮은 레이턴시
- AWS Console 우측 상단에서 "아시아 태평양(서울)" 선택

---

## 🔐 2단계: 보안 그룹 생성

### EC2 대시보드 접속
1. AWS Console → EC2 → 보안 그룹
2. "보안 그룹 생성" 클릭

### 보안 그룹 설정
**이름**: `daymemory-sg`

**인바운드 규칙**:
| 유형 | 프로토콜 | 포트 | 소스 | 설명 |
|------|----------|------|------|------|
| SSH | TCP | 22 | 내 IP | SSH 접속 |
| HTTP | TCP | 80 | 0.0.0.0/0 | 웹 접속 |
| HTTPS | TCP | 443 | 0.0.0.0/0 | 보안 웹 접속 |
| Custom TCP | TCP | 8080 | 0.0.0.0/0 | 백엔드 API (임시) |

⚠️ **보안 강화**: 배포 후 8080 포트는 제거하고 Nginx로만 접근

---

## 🖥️ 3단계: EC2 인스턴스 생성 (무료 티어)

### 인스턴스 시작
1. EC2 대시보드 → 인스턴스 → "인스턴스 시작"

### 설정값
- **이름**: `day-memory-server`
- **AMI**: Ubuntu Server 22.04 LTS (HVM), SSD Volume Type
- **인스턴스 유형**: **t2.micro** (프리티어 적용)
- **키 페어**: 새로 생성 (`daymemory-key.pem`) → **다운로드 후 안전하게 보관**
- **네트워크 설정**:
  - VPC: 기본 VPC
  - 퍼블릭 IP 자동 할당: 활성화
  - 보안 그룹: 위에서 생성한 `daymemory-sg` 선택
- **스토리지**: 30 GB gp3 (무료 티어 한도)

### Elastic IP 할당 (선택사항)
인스턴스 재시작 시 IP 변경 방지:
1. EC2 → 탄력적 IP → "탄력적 IP 주소 할당"
2. 할당된 IP → "탄력적 IP 주소 연결" → EC2 인스턴스 선택

---

## 🗄️ 4단계: RDS PostgreSQL 생성 (무료 티어)

### RDS 대시보드
1. AWS Console → RDS → "데이터베이스 생성"

### 설정값
- **엔진 옵션**: PostgreSQL 16
- **템플릿**: **프리 티어** ⭐
- **DB 인스턴스 식별자**: `daymemory-db`
- **마스터 사용자 이름**: `postgres`
- **마스터 암호**: 강력한 비밀번호 설정 (저장해두기!)

- **DB 인스턴스 클래스**: **db.t3.micro** (프리티어)
- **스토리지**: 20 GB gp2 (프리티어 최대)
- **스토리지 자동 조정**: 비활성화 (비용 절감)

- **VPC**: 기본 VPC
- **퍼블릭 액세스**: 아니요 (보안)
- **VPC 보안 그룹**: 새로 생성 (`daymemory-db-sg`)
  - 인바운드: PostgreSQL (5432), 소스: EC2 보안 그룹 (`daymemory-sg`)

- **데이터베이스 이름**: `daymemory`
- **백업 보존 기간**: 7일 (무료 티어 내)

### 엔드포인트 확인
생성 완료 후 엔드포인트 복사:
```
daymemory-db.xxxxxxxxx.ap-northeast-2.rds.amazonaws.com
```

---

## 🚀 5단계: EC2에 애플리케이션 배포

### SSH 접속
```bash
# 키 파일 권한 설정 (최초 1회)
chmod 400 daymemory-key.pem

# EC2 접속 (퍼블릭 IP는 EC2 대시보드에서 확인)
ssh -i daymemory-key.pem ubuntu@<EC2-퍼블릭-IP>
```

### Docker 설치
```bash
# 패키지 업데이트
sudo apt update && sudo apt upgrade -y

# Docker 설치
sudo apt install -y docker.io docker-compose

# 현재 사용자를 docker 그룹에 추가
sudo usermod -aG docker ubuntu

# 로그아웃 후 재접속하여 권한 적용
exit
ssh -i daymemory-key.pem ubuntu@<EC2-퍼블릭-IP>

# Docker 버전 확인
docker --version
docker-compose --version
```

### 애플리케이션 코드 배포
```bash
# Git 설치
sudo apt install -y git

# 코드 클론
git clone https://github.com/easter1201/day-memory.git
cd day-memory

# 환경 변수 설정
cp .env.example .env
nano .env
```

### .env 파일 설정
```bash
# Database (RDS 엔드포인트 사용)
DB_NAME=daymemory
DB_USERNAME=postgres
DB_PASSWORD=<RDS에서-설정한-마스터-암호>
DB_PORT=5432

# docker-compose.yml에서 DB_URL 직접 설정
# DB_URL=jdbc:postgresql://<RDS-엔드포인트>:5432/daymemory

# JWT (프로덕션용 강력한 시크릿)
JWT_SECRET=$(openssl rand -base64 48)
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=kite1201kr@gmail.com
MAIL_PASSWORD=your-email-app-password-here

# AI API
AI_PROVIDER=gemini
AI_API_KEY=your-gemini-api-key-here
AI_MODEL=gemini-2.5-flash

# Naver Shopping
NAVER_CLIENT_ID=iw9XgEVwrWdEIPtVGEDl
NAVER_CLIENT_SECRET=your-naver-client-secret-here

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

### docker-compose.yml 수정
RDS를 사용하므로 PostgreSQL 컨테이너 제거:

```bash
nano docker-compose.yml
```

**수정 내용**:
```yaml
services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: daymemory-backend
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-prod}
      DB_URL: jdbc:postgresql://<RDS-엔드포인트>:5432/${DB_NAME:-daymemory}
      DB_USERNAME: ${DB_USERNAME:-postgres}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_ACCESS_EXPIRATION: ${JWT_ACCESS_EXPIRATION:-3600000}
      JWT_REFRESH_EXPIRATION: ${JWT_REFRESH_EXPIRATION:-604800000}
      MAIL_HOST: ${MAIL_HOST:-smtp.gmail.com}
      MAIL_PORT: ${MAIL_PORT:-587}
      MAIL_USERNAME: ${MAIL_USERNAME}
      MAIL_PASSWORD: ${MAIL_PASSWORD}
      AI_API_KEY: ${AI_API_KEY}
      AI_PROVIDER: ${AI_PROVIDER:-gemini}
      AI_MODEL: ${AI_MODEL:-gemini-2.5-flash}
      NAVER_CLIENT_ID: ${NAVER_CLIENT_ID}
      NAVER_CLIENT_SECRET: ${NAVER_CLIENT_SECRET}
    ports:
      - "${SERVER_PORT:-8080}:8080"
    volumes:
      - backend_uploads:/app/uploads
      - backend_logs:/app/logs

volumes:
  backend_uploads:
    driver: local
  backend_logs:
    driver: local
```

### 애플리케이션 실행
```bash
# Docker 이미지 빌드 및 컨테이너 시작
docker-compose up -d --build

# 로그 확인
docker-compose logs -f backend

# 헬스 체크
curl http://localhost:8080/actuator/health
```

---

## 🌐 6단계: 프론트엔드 S3 + CloudFront 배포 (무료)

### S3 버킷 생성
1. AWS Console → S3 → "버킷 만들기"
2. **버킷 이름**: `daymemory-frontend` (전역 고유)
3. **리전**: 서울 (ap-northeast-2)
4. **퍼블릭 액세스 차단**: 모두 해제 (정적 웹사이트용)
5. 생성 완료

### 정적 웹사이트 호스팅 활성화
1. 버킷 선택 → 속성 → "정적 웹 사이트 호스팅"
2. **활성화** 선택
3. **인덱스 문서**: `index.html`
4. **오류 문서**: `index.html` (SPA 라우팅용)
5. 저장

### 버킷 정책 설정
권한 → 버킷 정책 → 편집:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::daymemory-frontend/*"
    }
  ]
}
```

### 프론트엔드 빌드 및 업로드

**로컬에서**:
```bash
cd frontend

# API URL을 EC2 퍼블릭 IP로 설정
# .env.production 파일 생성
echo "VITE_API_URL=http://<EC2-퍼블릭-IP>:8080/api" > .env.production

# 프로덕션 빌드
npx vite build

# AWS CLI 설치 (미설치 시)
# brew install awscli  # macOS
# pip install awscli   # Windows/Linux

# AWS CLI 설정
aws configure
# Access Key ID: IAM에서 생성
# Secret Access Key: IAM에서 생성
# Region: ap-northeast-2

# S3에 업로드
aws s3 sync dist/ s3://daymemory-frontend --delete
```

### CloudFront 배포 (선택사항, HTTPS용)
1. CloudFront → "배포 생성"
2. **원본 도메인**: S3 정적 웹사이트 엔드포인트 선택
3. **뷰어 프로토콜 정책**: Redirect HTTP to HTTPS
4. **기본 루트 객체**: `index.html`
5. 생성 (배포까지 10-15분 소요)

---

## 🔒 7단계: Nginx 설정 (HTTPS + 리버스 프록시)

### Nginx 설치
```bash
sudo apt install -y nginx certbot python3-certbot-nginx
```

### Nginx 설정
```bash
sudo nano /etc/nginx/sites-available/daymemory
```

**설정 내용**:
```nginx
server {
    listen 80;
    server_name <도메인-또는-EC2-IP>;

    # 프론트엔드는 S3/CloudFront 사용
    # 백엔드 API만 프록시
    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Actuator (헬스 체크)
    location /actuator {
        proxy_pass http://localhost:8080/actuator;
        proxy_set_header Host $host;
    }
}
```

### Nginx 활성화
```bash
sudo ln -s /etc/nginx/sites-available/daymemory /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### HTTPS 인증서 (도메인 있을 경우)
```bash
sudo certbot --nginx -d yourdomain.com
```

---

## 📊 8단계: 모니터링 및 비용 관리

### CloudWatch 알림 설정
1. CloudWatch → 경보 → "경보 생성"
2. **지표**: Billing → Total Estimated Charge
3. **조건**: 임계값 $5 초과 시 알림
4. **작업**: SNS로 이메일 전송

### 비용 절감 팁
1. **EC2 인스턴스 정지**: 사용하지 않을 때 정지 (EBS 비용만 발생)
2. **RDS 스냅샷**: 주기적으로 스냅샷 생성 후 인스턴스 삭제 가능
3. **CloudWatch 로그**: 로그 보존 기간 7일로 제한
4. **S3 수명 주기**: 오래된 파일 자동 삭제 정책

### 자동 시작/종료 (비용 절감)
Lambda 함수로 야간/주말 자동 정지:
```python
# Lambda 함수 (Python 3.x)
import boto3

def lambda_handler(event, context):
    ec2 = boto3.client('ec2', region_name='ap-northeast-2')

    # 인스턴스 ID
    instances = ['i-xxxxxxxxx']

    # 정지
    ec2.stop_instances(InstanceIds=instances)

    return {
        'statusCode': 200,
        'body': 'Instances stopped'
    }
```

EventBridge로 스케줄 설정:
- 시작: 평일 오전 9시
- 정지: 평일 오후 11시

---

## ✅ 최종 체크리스트

### 배포 완료 확인
- [ ] EC2 인스턴스 실행 중
- [ ] RDS 데이터베이스 사용 가능
- [ ] Docker 컨테이너 정상 실행
- [ ] 백엔드 헬스 체크 성공: `http://<EC2-IP>:8080/actuator/health`
- [ ] S3 프론트엔드 접근 가능
- [ ] API 호출 정상 작동

### 보안 강화
- [ ] SSH 키 안전하게 보관
- [ ] RDS 퍼블릭 액세스 비활성화
- [ ] EC2 보안 그룹에서 8080 포트 제거 (Nginx 사용 시)
- [ ] 환경 변수 파일 권한: `chmod 600 .env`
- [ ] JWT Secret 프로덕션용으로 변경
- [ ] DB 비밀번호 강력하게 설정

### 비용 모니터링
- [ ] CloudWatch 경보 설정 ($5 임계값)
- [ ] Billing Dashboard에서 현재 비용 확인
- [ ] 프리티어 사용량 모니터링

---

## 🆘 트러블슈팅

### EC2 SSH 접속 실패
```bash
# 키 파일 권한 확인
chmod 400 daymemory-key.pem

# 보안 그룹에서 내 IP의 SSH (22) 허용 확인
```

### RDS 연결 실패
```bash
# EC2에서 RDS 연결 테스트
sudo apt install -y postgresql-client
psql -h <RDS-엔드포인트> -U postgres -d daymemory

# 보안 그룹 확인: RDS는 EC2 보안 그룹에서만 5432 허용
```

### Docker 빌드 메모리 부족
```bash
# t2.micro는 메모리가 1GB로 부족할 수 있음
# 스왑 파일 생성
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# 영구 적용
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### 프론트엔드 API 호출 실패
- EC2 보안 그룹에서 80, 443, 8080 포트 개방 확인
- CORS 설정 확인
- `.env.production`에서 API URL 확인

---

## 📱 접속 URL

배포 완료 후:
- **프론트엔드**: http://daymemory-frontend.s3-website.ap-northeast-2.amazonaws.com
- **백엔드 API**: http://<EC2-퍼블릭-IP>/api
- **헬스 체크**: http://<EC2-퍼블릭-IP>/actuator/health

CloudFront 사용 시:
- **프론트엔드**: https://<CloudFront-도메인>.cloudfront.net
