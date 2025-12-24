# AWS Free Tier 배포 가이드

## 🆓 AWS 무료 요금제로 배포하기

### 사양
- **인스턴스 타입**: t2.micro (1GB RAM, 1vCPU)
- **EBS**: 30GB (충분함)
- **월 제한**: 750시간 (충분함)
- **비용**: 완전 무료 (1년)

---

## 📝 Step 1: 새 EC2 인스턴스 생성

### 1.1 AWS Console 접속
- https://console.aws.amazon.com → EC2

### 1.2 인스턴스 생성
1. **Instances** → **Launch instances** 클릭
2. **Name**: `day-memory-prod`
3. **AMI**: Ubuntu 24.04 LTS (free tier eligible)
4. **Instance type**: `t2.micro` ⭐ **필수**
5. **Key pair**: 새로 생성 (다운로드 받아서 안전하게 보관)
6. **Security Group**: 다음 포트 허용
   - 80 (HTTP)
   - 443 (HTTPS, 선택)
   - 22 (SSH, 현재만)
7. **Storage**: 30GB (기본값 유지)
8. **Launch** 클릭

### 1.3 인스턴스 실행 확인
- 새 퍼블릭 IP 주소 확인 (예: `54.180.XX.XX`)

---

## 🔧 Step 2: 새 EC2에 배포

### 2.1 EC2에 접속
**AWS Console** → **Instances** → 선택 → **Connect** → **EC2 Instance Connect**

또는 로컬 터미널:
```bash
ssh -i your-key.pem ubuntu@54.180.XX.XX
```

### 2.2 Docker & Docker Compose 설치
```bash
# 시스템 업데이트
sudo apt-get update && sudo apt-get upgrade -y

# Docker 설치
sudo apt-get install -y docker.io docker-compose

# Docker 서비스 시작
sudo systemctl start docker
sudo systemctl enable docker

# 사용자 권한 추가 (sudo 없이 docker 명령 사용)
sudo usermod -aG docker $USER
newgrp docker

# 확인
docker --version
docker-compose --version
```

### 2.3 코드 클론 및 배포
```bash
# 코드 클론
git clone https://github.com/easter1201/day-memory.git
cd day-memory

# 환경변수 설정 (NEW_IP를 실제 IP로 변경)
export NEW_IP="54.180.XX.XX"

# 배포
CORS_ALLOWED_ORIGINS="http://$NEW_IP" \
FRONTEND_API_BASE_URL="http://$NEW_IP/api" \
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# 확인
docker-compose -f docker-compose.yml -f docker-compose.prod.yml ps
```

### 2.4 상태 확인
```bash
# 모든 컨테이너 Up 상태인지 확인
# postgres, backend, frontend 모두 Up이어야 함

# 로그 확인
docker-compose logs -f backend
docker-compose logs -f frontend
```

---

## 🌐 Step 3: 접속 확인

브라우저에서:
```
http://54.180.XX.XX
```

접속 후:
1. 회원가입 시도
2. 로그인
3. 기본 기능 테스트

---

## 💡 운영 팁

### 자동 배포 활성화 (선택)
GitHub Actions로 자동 배포를 원하면:

1. **GitHub Secrets** 업데이트 (`EC2_SSH_KEY`)
2. 새 `.pem` 키 등록

### 모니터링
```bash
# 컨테이너 상태 실시간 확인
watch docker ps

# 로그 확인
docker-compose logs -f

# 디스크 사용량 확인
df -h
```

### 백업
```bash
# 데이터베이스 백업
docker-compose exec postgres pg_dump -U postgres daymemory_prod > backup.sql
```

---

## ⚠️ 주의사항

1. **메모리 부족**: t2.micro는 메모리가 1GB만 있음
   - 무거운 AI 기능은 제한될 수 있음
   - 필요시 t2.small으로 업그레이드 (유료)

2. **EBS 용량**: 30GB 초과 시 추가 요금 발생
   - 로그 자동 정리 설정됨 (10MB, 3파일 제한)

3. **무료 기간**: 12개월만 무료
   - 기간 이후 자동 요금 부과 주의

4. **Elastic IP**: 고정 IP 원하면 "Elastic IP" 할당 (무료)

---

## 🆘 문제 해결

### Docker가 실행 안 될 때
```bash
sudo systemctl restart docker
sudo systemctl status docker
```

### 포트 충돌
```bash
# 8080 사용 프로세스 확인
lsof -i :8080

# 필요시 docker-compose.prod.yml에서 포트 변경 (8081 등)
```

### 메모리 부족
```bash
# 메모리 사용량 확인
free -h

# 불필요한 이미지/컨테이너 정리
docker system prune -a
```

---

## 📊 예상 비용 (12개월)

- **t2.micro**: $0 (무료)
- **EBS 30GB**: $0 (무료)
- **데이터 전송**: $0 ~ $5 (월별 사용량에 따라)
- **총합**: **완전 무료** 또는 $5 이내

---

## ✅ 최종 체크리스트

- [ ] 새 EC2 인스턴스 생성 (t2.micro)
- [ ] 보안 그룹 설정 (포트 80, 22)
- [ ] Docker 설치
- [ ] 코드 클론
- [ ] 배포 실행
- [ ] 브라우저에서 접속 확인
- [ ] 회원가입 / 로그인 테스트
- [ ] 기본 기능 테스트

**배포 완료!** 🎉
