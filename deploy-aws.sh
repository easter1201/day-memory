#!/bin/bash

# AWS EC2 배포 스크립트
# 사용법: ./deploy-aws.sh

set -e

echo "========================================="
echo "  Day Memory AWS 배포 스크립트"
echo "========================================="
echo ""

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 환경 변수 확인
if [ ! -f .env ]; then
    echo -e "${RED}❌ .env 파일이 없습니다!${NC}"
    echo "먼저 .env 파일을 생성하세요:"
    echo "  cp .env.example .env"
    echo "  nano .env  # 실제 값으로 수정"
    exit 1
fi

echo -e "${GREEN}✓ .env 파일 확인 완료${NC}"

# Git 최신 코드 가져오기
echo ""
echo "📥 최신 코드 가져오기..."
git pull origin main

# Docker Compose 실행
echo ""
echo "🐳 Docker 컨테이너 시작..."
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d --build

# 로그 확인
echo ""
echo "📋 배포 로그 확인 중..."
sleep 5
docker-compose -f docker-compose.prod.yml logs --tail=50 backend

# 헬스 체크
echo ""
echo "🏥 헬스 체크 중..."
sleep 10

if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ 배포 성공!${NC}"
    echo ""
    echo "접속 URL:"
    echo "  - API: http://$(curl -s http://checkip.amazonaws.com):8080/api"
    echo "  - Health: http://$(curl -s http://checkip.amazonaws.com):8080/actuator/health"
else
    echo -e "${RED}❌ 헬스 체크 실패${NC}"
    echo "로그를 확인하세요:"
    echo "  docker-compose -f docker-compose.prod.yml logs backend"
    exit 1
fi

echo ""
echo "========================================="
echo -e "${GREEN}  배포 완료!${NC}"
echo "========================================="
