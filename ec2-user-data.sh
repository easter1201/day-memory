#!/bin/bash
set -e

# 로그 출력
exec > >(tee -a /var/log/user-data.log)
exec 2>&1

echo "========== EC2 User Data Script Started =========="

# Docker 설치 (필요시)
if ! command -v docker &> /dev/null; then
    echo "Installing Docker..."
    amazon-linux-extras install -y docker
    systemctl start docker
    systemctl enable docker
    usermod -a -G docker ec2-user
fi

# Docker Compose 설치 (필요시)
if ! command -v docker-compose &> /dev/null; then
    echo "Installing Docker Compose..."
    curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
fi

# 작업 디렉토리 준비
cd /home/ec2-user
if [ -d "day-memory" ]; then
    echo "Updating repository..."
    cd day-memory
    git pull origin main
else
    echo "Cloning repository..."
    git clone https://github.com/easter1201/day-memory.git
    cd day-memory
fi

# .env 파일 생성 (기존 값 유지)
echo "Setting up environment variables..."
cat > .env << 'EOF'
DB_NAME=daymemory
DB_USERNAME=postgres
DB_PASSWORD=postgres
CORS_ALLOWED_ORIGINS=http://$(hostname -I | awk '{print $1}')
JWT_SECRET=secret123456789012345678901234567890123456789
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=daymemoryadmin@gmail.com
MAIL_PASSWORD=bdpq qkhb wxhx mrvq
AI_API_KEY=AIzaSyDJT8v01qtyXfZIzR_yBQf1jMX4r9LODbI
AI_PROVIDER=gemini
AI_MODEL=gemini-2.5-flash
GOOGLE_CLIENT_ID=1087491850820-v78cp8e6o6f9msk7ghof6t4nh2k5rp1a.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=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
KAKAO_CLIENT_ID=f41c7a76e3f0ba5a90e1d8e7e8c5c4b8
KAKAO_CLIENT_SECRET=qs8F7l8Z9K0L1M2N3O4P5Q6R7S8T9U0V
NAVER_CLIENT_ID=U49fRSRN6Z4bWvPw5ZC5
NAVER_CLIENT_SECRET=3P2EtqKkCG
S3_BUCKET_NAME=daymemory-uploads
AWS_REGION=ap-northeast-2
EOF

# Docker Hub에서 이미지 pull
echo "Pulling Docker images from Docker Hub..."
docker pull easter1201/day-memory-backend:latest
docker pull easter1201/day-memory-frontend:latest

# docker-compose 실행
echo "Starting containers with docker-compose..."
docker-compose -f docker-compose.prod.yml up -d

echo "========== EC2 User Data Script Completed =========="
