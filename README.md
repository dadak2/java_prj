# 🚀 Cursor Project

Cursor활용 Spring Boot 기반의 사용자 인증 및 게시판 시스템

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
- **Lombok**

### Database
- **H2 Database** (개발용)
- **MariaDB** (운영용)

### Frontend
- **HTML5**
- **CSS3**
- **JavaScript (ES6+)**

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

### 3. 보안 기능
- **BCrypt 비밀번호 암호화**
- **CSRF 보호 비활성화** (개발 환경)
- **정적 리소스 접근 허용**
- **API 엔드포인트 보안 설정**


## 📱 주요 페이지

### 1. 로그인 페이지 

### 2. 회원가입 페이지 

### 3. 게시판 페이지 

### 4. 게시글 작성 페이지 

### 5. 게시글 상세 페이지 


## 🧪 테스트

프로젝트에는 다음과 같은 테스트 코드가 포함되어 있습니다:

- **ConnectionTest**: 데이터베이스 연결 테스트
- **BoardControllerTest**: 게시판 컨트롤러 테스트
- **UserTest**: 사용자 엔티티 테스트
- **UserRepositoryTest**: 사용자 리포지토리 테스트
- **BoardServiceTest**: 게시판 서비스 테스트
- **UserServiceTest**: 사용자 서비스 테스트

---

**Cursor Project** - Spring Boot 기반 웹 애플리케이션





