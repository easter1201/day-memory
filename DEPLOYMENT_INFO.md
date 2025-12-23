# Day Memory - 배포 정보

## 🚀 배포 환경

### 프로덕션 서버
- **URL**: http://54.180.29.118
- **백엔드 API**: http://54.180.29.118:8080/api
- **헬스 체크**: http://54.180.29.118:8080/actuator/health

### AWS 인프라
- **EC2**: t2.micro (Ubuntu 24.04 LTS)
- **RDS**: db.t3.micro (PostgreSQL 16.4)
- **리전**: ap-northeast-2 (서울)

## 🔄 CI/CD

### 자동 배포
- **트리거**: main 브랜치 push
- **플랫폼**: GitHub Actions
- **워크플로우**: `.github/workflows/deploy.yml`

### 배포 프로세스
1. Backend: Git pull → Docker rebuild
2. Frontend: Build → EC2 업로드 → Nginx reload
3. Health Check: API 및 Frontend 응답 확인

### 수동 배포
GitHub Actions 페이지에서 "Run workflow" 버튼으로 수동 실행 가능

## 📊 모니터링
- **GitHub Actions**: https://github.com/easter1201/day-memory/actions
- **백엔드 로그**: `docker logs daymemory-backend`
- **Nginx 로그**: `/var/log/nginx/`

## 💰 비용
- **EC2 t2.micro**: 무료 (프리 티어 750시간/월)
- **RDS db.t3.micro**: 무료 (프리 티어 750시간/월)
- **총 예상 비용**: $0/월 (1년간)

---

**마지막 업데이트**: 2025-01-17
**배포 버전**: v1.0.0
