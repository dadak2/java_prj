# Java Spring Boot 프로젝트 개발 계획

## 📋 프로젝트 개요
- **프로젝트명**: Java Spring Boot 웹 애플리케이션
- **기술 스택**: Spring Boot 3.5.4, Java 17, JPA, MySQL/H2
- **주요 기능**: 회원가입, 로그인, 게시판 CRUD
- **개발 방식**: TDD (Test Driven Development)

## 🎯 개발 목표
1. 회원가입 및 로그인 기능 구현
2. 게시판 CRUD 기능 구현
3. 보안 및 검증 기능 추가
4. 페이징 및 검색 기능 구현

---

## 📅 개발 순서 (TDD 방식)

### 1단계: 데이터베이스 설계 및 엔티티 생성

#### 1.1 User 엔티티 설계
- [ ] User 엔티티 클래스 생성
- [ ] 회원 정보 필드 정의 (id, username, password, email, role, createdAt, updatedAt)
- [ ] 보안을 위한 비밀번호 암호화 설정
- [ ] Validation 어노테이션 추가

#### 1.2 Board 엔티티 설계
- [ ] Board 엔티티 클래스 생성
- [ ] 게시글 정보 필드 정의 (id, title, content, author, viewCount, createdAt, updatedAt)
- [ ] User와의 관계 설정 (ManyToOne)
- [ ] Validation 어노테이션 추가

#### 1.3 Comment 엔티티 설계 (선택사항)
- [ ] Comment 엔티티 클래스 생성
- [ ] 댓글 정보 필드 정의 (id, content, author, createdAt)
- [ ] Board와 User와의 관계 설정

### 2단계: Repository 계층 구현

#### 2.1 UserRepository
- [ ] UserRepository 인터페이스 생성
- [ ] findByUsername 메서드 구현
- [ ] findByEmail 메서드 구현
- [ ] 중복 검사 기능 구현

#### 2.2 BoardRepository
- [ ] BoardRepository 인터페이스 생성
- [ ] 페이징 처리 기능 구현
- [ ] 정렬 기능 구현
- [ ] 검색 기능 구현 (제목, 내용)

### 3단계: Service 계층 구현

#### 3.1 UserService
- [ ] UserService 클래스 생성
- [ ] 회원가입 로직 구현
- [ ] 로그인 로직 구현
- [ ] 비밀번호 암호화/검증 구현
- [ ] 중복 검사 로직 구현

#### 3.2 BoardService
- [ ] BoardService 클래스 생성
- [ ] 게시글 CRUD 로직 구현
- [ ] 조회수 증가 로직 구현
- [ ] 페이징 처리 로직 구현

### 4단계: Controller 계층 구현

#### 4.1 UserController
- [ ] UserController 클래스 생성
- [ ] 회원가입 API 구현
- [ ] 로그인 API 구현
- [ ] 회원 정보 조회/수정 API 구현

#### 4.2 BoardController
- [ ] BoardController 클래스 생성
- [ ] 게시글 목록 API 구현
- [ ] 게시글 상세보기 API 구현
- [ ] 게시글 작성 API 구현
- [ ] 게시글 수정 API 구현
- [ ] 게시글 삭제 API 구현

### 5단계: 보안 및 검증

#### 5.1 Spring Security 설정
- [ ] Spring Security 의존성 추가
- [ ] SecurityConfig 클래스 생성
- [ ] 비밀번호 암호화 설정
- [ ] 세션 관리 설정

#### 5.2 Validation 추가
- [ ] Bean Validation 의존성 추가
- [ ] 입력값 검증 로직 구현
- [ ] Global Exception Handler 구현
- [ ] 에러 응답 형식 정의

---

## 🗄️ 데이터베이스 구조 설계

### 1. User 테이블
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. Board 테이블
```sql
CREATE TABLE boards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    view_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id)
);
```

### 3. Comment 테이블 (선택사항)
```sql
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    board_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES boards(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);
```

---

## 🔧 기술적 고려사항

### 보안
- **비밀번호 암호화**: BCrypt 사용
- **세션 관리**: Spring Security Session
- **입력값 검증**: Bean Validation

### 성능
- **페이징**: Spring Data JPA Pageable
- **인덱스**: username, email, board_id에 인덱스 추가
- **캐싱**: Redis (선택사항)

### 사용자 경험
- **에러 처리**: Global Exception Handler
- **응답 형식**: 일관된 JSON 응답 구조
- **검색 기능**: 제목, 내용 기반 검색

---

## ⚡ 구현 우선순위

### 높음 (필수)
1. User 엔티티 및 회원가입
2. Board 엔티티 및 기본 CRUD
3. 기본 보안 설정

### 중간 (권장)
1. 로그인 기능
2. 페이징 처리
3. 검색 기능

### 낮음 (선택)
1. 댓글 기능
2. 파일 업로드
3. 고급 검색

---

## 📝 개발 노트

### 완료된 작업
- [x] Spring Boot 프로젝트 설정
- [x] 데이터베이스 연결 설정
- [x] GitHub 저장소 생성 및 연결
- [x] README.md 작성

### 진행 중인 작업
- [ ] User 엔티티 설계 및 구현

### 다음 작업
- [ ] Board 엔티티 설계 및 구현
- [ ] Repository 계층 구현

---

## 🔗 관련 링크
- **GitHub 저장소**: https://github.com/dadak2/java_prj
- **로컬 프로젝트**: E:\cursor
- **데이터베이스**: MySQL (cursor_db)

---

## 📅 예상 일정
- **1일차**: User 엔티티 및 회원가입 기능
- **2일차**: Board 엔티티 및 게시판 CRUD
- **3일차**: 보안 설정 및 검증
- **4일차**: 페이징 및 검색 기능
- **5일차**: 테스트 및 최적화 