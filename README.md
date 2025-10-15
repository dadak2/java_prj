# 🚀 Cursor Project

Cursor활용 Spring Boot 기반의 사용자 인증, 게시판 시스템 및 실시간 랭킹 게임

## 📋 프로젝트 개요

Cursor Project는 Spring Boot 3.5.4를 기반으로 한 웹 애플리케이션으로, 사용자 인증 시스템과 게시판 기능을 제공
JPA를 사용한 데이터베이스 연동과 Spring Security를 통한 보안 기능을 포함

## 🛠 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.5.4-SNAPSHOT**
- **Spring Data JPA**
- **Spring Security**
- **Spring Validation**
- **Spring WebSocket**
- **Spring Kafka**
- **Spring Data Redis**
- **Lombok**

### Database
- **H2 Database** (개발용)
- **MariaDB** (운영용)
- **Redis** (실시간 랭킹)

### Message Queue
- **Apache Kafka** (비동기 점수 처리)

### Frontend
- **HTML5**
- **CSS3**
- **JavaScript (ES6+)**
- **WebSocket (SockJS + STOMP)**

### Build Tool
- **Gradle**

## 🏗 프로젝트 구조

```
cursor/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── config/           # 설정 클래스
│   │   │   │   
│   │   │   ├── controller/       # 컨트롤러
│   │   │   │  
│   │   │   ├── dto/             # 데이터 전송 객체
│   │   │   │  
│   │   │   ├── entity/          # 엔티티 클래스
│   │   │   │   
│   │   │   ├── repository/      # 리포지토리
│   │   │   │   
│   │   │   ├── service/         # 서비스 클래스
│   │   │   │   
│   │   │   └── CursorApplication.java
│   │   └── resources/
│   │       └── static/          # 정적 리소스
│   │           ├── css/         # 스타일시트
│   │           ├── js/          # 자바스크립트
│   │           ├── board.html   # 게시판 페이지
│   │           ├── detail.html  # 게시글 상세 페이지
│   │           ├── index.html   # 메인 페이지
│   │           ├── login.html   # 로그인 페이지
│   │           ├── signup.html  # 회원가입 페이지
│   │           └── write.html   # 게시글 작성 페이지
│   └── test/                    # 테스트 코드
└── build.gradle
```

## 🚀 주요 기능

### 1. 사용자 관리 시스템
- **회원가입**: 이메일, 닉네임, 비밀번호를 통한 계정 생성
- **로그인/로그아웃**: Spring Security 기반 인증
- **사용자 역할 관리**: USER, ADMIN 역할 구분
- **계정 활성화 상태 관리**: 활성/비활성 계정 구분

### 2. 게시판 시스템
- **게시글 목록 조회**: 페이징 처리된 게시글 목록
- **게시글 상세 조회**: 개별 게시글 내용 및 메타데이터
- **게시글 작성**: 인증된 사용자만 게시글 작성 가능
- **게시글 수정/삭제**: 작성자만 수정/삭제 가능
- **카테고리 분류**: 게시글 카테고리별 분류
- **조회수, 좋아요, 댓글 수 관리**

### 3. 게임 시스템
- **스네이크 게임**: HTML5 Canvas 기반 웹 게임
- **실시간 점수 수집**: 게임 종료 시 자동 점수 제출
- **비동기 처리**: Kafka를 통한 점수 이벤트 처리
- **실시간 랭킹**: Redis 기반 실시간 랭킹 시스템
- **WebSocket 통신**: 실시간 랭킹 업데이트

### 4. 보안 기능
- **BCrypt 비밀번호 암호화**
- **CSRF 보호 비활성화** (개발 환경)
- **정적 리소스 접근 허용**
- **API 엔드포인트 보안 설정**


## 📱 주요 페이지

### 1. 로그인 페이지 
- 사용자 인증을 위한 로그인 폼
- 이메일과 비밀번호 입력
- 회원가입 페이지로 이동 링크

### 2. 회원가입 페이지 
- 새로운 사용자 계정 생성
- 이메일, 닉네임, 비밀번호 입력
- 유효성 검사 및 중복 확인

### 3. 게시판 페이지 
- 게시글 목록 표시
- 페이징 처리
- 카테고리별 필터링
- 게시글 작성 버튼

### 4. 게시글 작성 페이지 
- 새로운 게시글 작성
- 제목, 내용, 카테고리 입력
- 유효성 검사

### 5. 게시글 상세 페이지 
- 개별 게시글 내용 표시
- 작성자 정보 (마스킹 처리)
- 조회수, 좋아요 수 표시
- 수정/삭제 기능 (작성자만)

### 6. 게임 페이지 
- 스네이크 게임 플레이
- 실시간 점수 표시
- 실시간 랭킹 보기
- 게임 컨트롤 (시작/일시정지/리셋)

## 🔍 최종 점검 시나리오 (내일 진행)

### 1. 사용자 관리 시스템 점검
- [ ] **회원가입**: 새 계정 생성 → 이메일/닉네임 중복 검증
- [ ] **로그인**: 정상 로그인 → 세션 생성 확인
- [ ] **로그아웃**: 세션 종료 확인
- [ ] **비밀번호 암호화**: BCrypt 적용 확인

### 2. 게시판 시스템 점검
- [ ] **게시글 작성**: 로그인 후 글쓰기 → 저장 확인
- [ ] **게시글 목록**: 페이징, 카테고리 필터링
- [ ] **게시글 상세**: 조회수 증가, 작성자 정보 마스킹
- [ ] **게시글 수정**: 작성자만 수정 가능
- [ ] **게시글 삭제**: 작성자만 삭제 가능

### 3. 보안 기능 점검
- [ ] **인증 없이 접근**: 보호된 페이지 접근 차단
- [ ] **CSRF 보호**: 개발환경에서 비활성화 확인
- [ ] **정적 리소스**: CSS/JS 파일 접근 허용

### 4. 데이터베이스 점검
- [ ] **테이블 생성**: users, boards 테이블 구조 확인
- [ ] **데이터 CRUD**: 생성/조회/수정/삭제 정상 동작

### 5. API 엔드포인트 점검
- [ ] **POST /api/auth/signup**: 회원가입 API
- [ ] **POST /api/auth/login**: 로그인 API
- [ ] **GET /api/boards**: 게시글 목록 API
- [ ] **POST /api/boards**: 게시글 작성 API
- [ ] **GET /api/boards/{id}**: 게시글 상세 API

### 6. 게임 시스템 점검
- [ ] **게임 플레이**: 스네이크 게임 정상 동작
- [ ] **점수 제출**: 게임 종료 시 점수 자동 제출
- [ ] **실시간 랭킹**: WebSocket을 통한 실시간 랭킹 업데이트
- [ ] **Redis 연동**: 랭킹 데이터 저장/조회
- [ ] **Kafka 연동**: 점수 이벤트 비동기 처리

### 7. 프론트엔드 점검
- [ ] **반응형 디자인**: 모바일/데스크톱 호환성
- [ ] **JavaScript 동작**: AJAX 요청 정상 처리
- [ ] **CSS 스타일링**: UI/UX 일관성 확인
- [ ] **WebSocket 연결**: 실시간 통신 정상 동작

## 🚀 실행 가이드

### 1. 필수 요구사항
- **Java 17** 이상
- **Redis** (포트 6379)
- **Apache Kafka** (포트 9092)
- **MariaDB** (포트 3306) 또는 **H2 Database**

### 2. Redis 설치 및 실행
```bash
# Windows (Chocolatey 사용)
choco install redis-64
redis-server

# 또는 Docker 사용
docker run -d -p 6379:6379 redis:latest
```

### 3. Kafka 설치 및 실행
```bash
# Kafka 다운로드 후 실행
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

# 토픽 생성
bin/kafka-topics.sh --create --topic game-scores --bootstrap-server localhost:9092
```

### 4. 애플리케이션 실행
```bash
# 프로젝트 빌드
./gradlew clean build

# 애플리케이션 실행
./gradlew bootRun
```

### 5. 접속
- **메인 페이지**: http://localhost:9090
- **게임 페이지**: http://localhost:9090/game.html
- **게시판**: http://localhost:9090/board.html

---

**Cursor Project** - Spring Boot 기반 웹 애플리케이션





