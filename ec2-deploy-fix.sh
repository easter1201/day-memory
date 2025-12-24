#!/bin/bash

# EC2 배포 문제 진단 및 수정 스크립트

echo "=== EC2 배포 상태 진단 시작 ==="
echo ""

# 1. 기본 시스템 상태
echo "1. 시스템 상태 확인..."
echo "   - 메모리: $(free -h | grep Mem)"
echo "   - 디스크: $(df -h / | tail -1)"
echo ""

# 2. Docker 상태
echo "2. Docker 상태 확인..."
echo "   - Docker 서비스: $(systemctl is-active docker)"
echo "   - 이미지:"
docker images | grep -E "day-memory|REPOSITORY"
echo "   - 컨테이너:"
docker ps -a
echo ""

# 3. 코드 디렉토리 확인
echo "3. 코드 디렉토리 확인..."
cd /home/ubuntu/day-memory
echo "   - 현재 위치: $(pwd)"
echo "   - .env 파일: $([ -f .env ] && echo '있음' || echo '없음')"
echo "   - docker-compose.prod.yml: $([ -f docker-compose.prod.yml ] && echo '있음' || echo '없음')"
echo ""

# 4. 스왑 설정 확인
echo "4. 스왑 메모리 설정..."
if [ ! -f /swapfile ]; then
    echo "   - 스왑 파일 생성 중..."
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo "   - 스왑 생성 완료"
else
    echo "   - 스왑: $(swapon --show)"
fi
echo ""

# 5. 기존 컨테이너 정지
echo "5. 기존 컨테이너 정지 중..."
sudo docker-compose -f docker-compose.prod.yml down 2>/dev/null || true
echo "   - 정지 완료"
echo ""

# 6. Docker 다시 빌드 (프론트엔드만 먼저)
echo "6. Docker 이미지 빌드 시작..."
echo "   - 프론트엔드 빌드 중..."
sudo docker build -f frontend/Dockerfile -t daymemory-frontend:latest frontend/

echo ""
echo "7. Docker Compose 실행 중..."
sudo docker-compose -f docker-compose.prod.yml up -d --build

echo ""
echo "8. 배포 상태 확인..."
sleep 15

echo "   - 실행 중인 컨테이너:"
sudo docker-compose -f docker-compose.prod.yml ps

echo ""
echo "9. 로그 확인 (마지막 50줄):"
sudo docker-compose -f docker-compose.prod.yml logs --tail 50

echo ""
echo "=== 진단 완료 ==="
echo ""
echo "접속 확인:"
echo "- 백엔드: curl http://localhost:8080/actuator/health"
echo "- 프론트엔드: curl http://localhost"
