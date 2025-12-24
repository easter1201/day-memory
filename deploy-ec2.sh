#!/bin/bash

# AWS EC2 배포 자동화 스크립트
# RDS 엔드포인트와 환경 변수를 설정하고 Docker로 배포

set -e

echo "=== Day Memory AWS EC2 배포 시작 ==="

# 환경 변수
RDS_ENDPOINT="daymemory-db.crqya82ugsxx.ap-northeast-2.rds.amazonaws.com"
DB_NAME="daymemory"
DB_USERNAME="postgres"
DB_PASSWORD="postgres"
EC2_IP="15.164.212.141"

echo "✓ 환경 변수 설정 완료"
echo "  - RDS Endpoint: $RDS_ENDPOINT"
echo "  - EC2 IP: $EC2_IP"

# 1. 패키지 업데이트 및 Docker 설치
echo ""
echo "=== 1. Docker 설치 중... ==="
sudo apt update && sudo apt upgrade -y
sudo apt install -y docker.io docker-compose git

# 2. Docker 그룹에 사용자 추가
echo ""
echo "=== 2. Docker 권한 설정 중... ==="
sudo usermod -aG docker ubuntu
sudo chmod 666 /var/run/docker.sock

# 3. 메모리 스왑 설정 (t2.micro 최적화)
echo ""
echo "=== 3. 스왑 파일 설정 중... ==="
if [ ! -f /swapfile ]; then
  sudo fallocate -l 2G /swapfile
  sudo chmod 600 /swapfile
  sudo mkswap /swapfile
  sudo swapon /swapfile
  echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
  echo "✓ 스왑 파일 생성 완료 (2GB)"
else
  echo "✓ 스왑 파일 이미 존재"
fi

# 4. 코드 클론
echo ""
echo "=== 4. 코드 클론 중... ==="
cd /home/ubuntu
if [ ! -d "day-memory" ]; then
  git clone https://github.com/easter1201/day-memory.git
  cd day-memory
else
  cd day-memory
  git pull origin main
fi

# 5. .env 파일 생성
echo ""
echo "=== 5. 환경 변수 파일 생성 중... ==="
cat > .env << EOF
# Database Configuration
DB_NAME=$DB_NAME
DB_USERNAME=$DB_USERNAME
DB_PASSWORD=$DB_PASSWORD
DB_URL=jdbc:postgresql://$RDS_ENDPOINT:5432/$DB_NAME

# CORS & API
CORS_ALLOWED_ORIGINS=http://$EC2_IP
FRONTEND_API_BASE_URL=http://$EC2_IP/api

# JWT Configuration
JWT_SECRET=$(openssl rand -base64 48)
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

# Email Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=kite1201kr@gmail.com
MAIL_PASSWORD=ckimofxhgocstvwu

# AI Configuration
AI_PROVIDER=gemini
AI_API_KEY=AIzaSyAnBXOppRPYbiiwmOihxnX8PDuXka9gTF0
AI_MODEL=gemini-2.5-flash

# Naver Shopping
NAVER_CLIENT_ID=iw9XgEVwrWdEIPtVGEDl
NAVER_CLIENT_SECRET=DnfZhivL2M

# Server Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
EOF

chmod 600 .env
echo "✓ .env 파일 생성 완료"

# 6. RDS 연결 테스트
echo ""
echo "=== 6. RDS 연결 테스트 중... ==="
sudo apt install -y postgresql-client
if psql -h $RDS_ENDPOINT -U $DB_USERNAME -d $DB_NAME -c "SELECT 1" 2>/dev/null; then
  echo "✓ RDS 연결 성공"
else
  echo "⚠ RDS 연결 실패 - 보안 그룹 설정 확인 필요"
  echo "  RDS 보안 그룹 인바운드에 EC2 보안 그룹의 5432 포트 허용 필수"
fi

# 7. Docker 빌드 및 실행
echo ""
echo "=== 7. Docker 빌드 및 실행 중... ==="
echo "⚠ 주의: 빌드에 5-10분 소요될 수 있습니다 (t2.micro 메모리 최적화 중)"

# 기존 컨테이너 정지 및 제거
docker-compose down 2>/dev/null || true

# 새 빌드 시작
docker-compose -f docker-compose.prod.yml up -d --build

echo ""
echo "=== 8. 배포 완료 및 헬스 체크 ==="
sleep 10

# 헬스 체크
echo "헬스 체크 시도 중..."
for i in {1..30}; do
  if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✓ 백엔드 정상 실행 중"
    curl -s http://localhost:8080/actuator/health | jq '.'
    break
  else
    echo "⏳ 대기 중... ($i/30)"
    sleep 2
  fi
done

echo ""
echo "=== 배포 완료! ==="
echo ""
echo "📝 접속 정보:"
echo "  - 백엔드 API: http://$EC2_IP:8080"
echo "  - 헬스 체크: http://$EC2_IP:8080/actuator/health"
echo ""
echo "🐳 Docker 로그 확인:"
echo "  docker-compose logs -f backend"
echo ""
echo "🛑 서비스 정지:"
echo "  docker-compose down"
echo ""
