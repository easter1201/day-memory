# GitHub Actions Workflows

Day Memory 프로젝트의 CI/CD 워크플로우 문서입니다.

## 📋 워크플로우 목록

### 1. CI - Build and Test (`ci.yml`)

**트리거**:
- `main`, `develop` 브랜치에 push
- Pull Request 생성/업데이트

**작업**:
- ✅ **Test Job**:
  - PostgreSQL 서비스 컨테이너 실행
  - Gradle로 모든 테스트 실행
  - 테스트 결과 리포트 생성 및 업로드

- ✅ **Build Job**:
  - Gradle로 JAR 빌드
  - 빌드 아티팩트 업로드 (7일 보관)

**환경**:
- Java 17 (Temurin)
- PostgreSQL 16 Alpine
- Ubuntu Latest

**출력물**:
- 테스트 리포트 (`test-reports`)
- JAR 파일 (`backend-jar`)

---

### 2. Docker Build and Push (`docker-build.yml`)

**트리거**:
- `main` 브랜치에 push
- 태그 생성 (`v*.*.*`)
- 수동 실행 (`workflow_dispatch`)

**작업**:
- Docker Buildx 설정
- GitHub Container Registry (ghcr.io) 로그인
- 멀티 플랫폼 이미지 빌드 (amd64, arm64)
- 이미지 태깅 및 푸시
- GitHub Actions 캐시 활용

**이미지 태그**:
- `main` - 최신 메인 브랜치
- `v1.0.0` - Semantic 버전
- `sha-abc1234` - 커밋 SHA

**레지스트리**: `ghcr.io/{owner}/{repo}`

---

### 3. Code Quality & Security (`code-quality.yml`)

**트리거**:
- `main`, `develop` 브랜치에 push
- Pull Request 생성/업데이트
- 매주 일요일 자정 (정기 스캔)

**작업**:
- ✅ **Dependency Check**: Gradle 의존성 확인
- ✅ **Docker Security Scan**: Trivy로 이미지 취약점 스캔
- ✅ **Gradle Build Scan**: 빌드 성능 및 이슈 분석

**보안**:
- SARIF 형식으로 취약점 리포트
- GitHub Security 탭에 결과 업로드

---

## 🚀 사용 방법

### CI 워크플로우 실행

```bash
# main 브랜치에 푸시
git push origin main

# Pull Request 생성
gh pr create --title "Feature: Add new API" --body "Description"
```

### Docker 이미지 빌드

```bash
# 태그 생성하여 버전 릴리스
git tag v1.0.0
git push origin v1.0.0

# 수동 실행
gh workflow run docker-build.yml
```

### 이미지 Pull

```bash
# GitHub Container Registry에서 이미지 다운로드
docker pull ghcr.io/{owner}/day-memory:main
```

---

## 📊 워크플로우 상태 확인

### GitHub UI
1. 저장소 페이지 → **Actions** 탭
2. 워크플로우 선택 → 실행 내역 확인
3. 각 Job 클릭 → 상세 로그 확인

### CLI (GitHub CLI)
```bash
# 워크플로우 목록
gh workflow list

# 최근 실행 내역
gh run list

# 특정 워크플로우 실행 보기
gh run view

# 워크플로우 로그 확인
gh run view --log
```

---

## 🔧 시크릿 설정

### 필수 시크릿

워크플로우는 기본적으로 `GITHUB_TOKEN`을 사용합니다 (자동 생성).

### 추가 시크릿 (선택사항)

Docker Hub 사용 시:
```
Settings → Secrets and variables → Actions → New repository secret
```

필요한 시크릿:
- `DOCKER_USERNAME`: Docker Hub 사용자명
- `DOCKER_PASSWORD`: Docker Hub 액세스 토큰

---

## 📈 성능 최적화

### Gradle 캐싱
- GitHub Actions에서 Gradle 의존성 자동 캐싱
- 빌드 시간 단축 (첫 실행: ~2분, 이후: ~30초)

### Docker Layer 캐싱
- GitHub Actions 캐시 활용 (`cache-from`, `cache-to`)
- 이미지 빌드 시간 단축

### 병렬 실행
- Test와 Build는 순차 실행 (의존성)
- 여러 PR 동시 실행 가능

---

## 🛡️ 보안

### 자동 보안 스캔
- **Trivy**: Docker 이미지 취약점 스캔
- **Dependency Check**: Gradle 의존성 취약점
- **GitHub Security**: 결과를 Security 탭에 표시

### 권한 관리
- 최소 권한 원칙 적용
- `contents: read`, `packages: write`

---

## 📝 배지 추가

README.md에 워크플로우 상태 배지 추가:

```markdown
![CI](https://github.com/{owner}/{repo}/workflows/CI%20-%20Build%20and%20Test/badge.svg)
![Docker](https://github.com/{owner}/{repo}/workflows/Docker%20Build%20and%20Push/badge.svg)
![Security](https://github.com/{owner}/{repo}/workflows/Code%20Quality%20%26%20Security/badge.svg)
```

---

## 🔍 트러블슈팅

### 테스트 실패
```bash
# 로컬에서 테스트 실행
cd backend
./gradlew test

# PostgreSQL 연결 확인
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up postgres
```

### Docker 빌드 실패
```bash
# 로컬에서 Docker 빌드
docker build -t daymemory-backend:test ./backend

# 캐시 없이 빌드
docker build --no-cache -t daymemory-backend:test ./backend
```

### 권한 오류
```bash
# gradlew 실행 권한 확인
ls -la backend/gradlew

# 권한 부여
chmod +x backend/gradlew
git add backend/gradlew
git commit -m "Fix gradlew permissions"
```

---

## 📚 참고 자료

- [GitHub Actions 문서](https://docs.github.com/en/actions)
- [Gradle GitHub Actions](https://github.com/gradle/gradle-build-action)
- [Docker Build Push Action](https://github.com/docker/build-push-action)
- [Trivy Security Scanner](https://github.com/aquasecurity/trivy-action)
