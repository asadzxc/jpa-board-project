#  JPA 게시판 프로젝트

이 프로젝트는 **Spring Boot + JPA** 기반의 간단한 게시판 웹 애플리케이션입니다.  
회원 기능, 게시글, 댓글 CRUD, 관리자 대시보드 등 **웹 서비스의 기본 요소들을 전반적으로 다룹니다.**

---

##  사용 기술 스택

- Java 17
- Spring Boot
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven

---

##  테스트용 계정

| 역할     | ID         | PW     |
|----------|------------|--------|
| 관리자   | admin      | 1234   |
| 일반회원 | kim80777   | 1234   |

---

##  주요 기능 요약

###  사용자 기능

- **회원가입**: 이름, 아이디, 비밀번호 입력
- **로그인 / 로그아웃**: 세션 기반 인증 처리
- **내 정보 페이지**: 회원가입 직후 바로 확인 가능
- **게시글 작성**: 로그인한 사용자만 가능
- **게시글 목록**: 전체 조회, 내가 쓴 글만 보기 필터링 가능
- **게시글 상세 보기**: 댓글 작성 및 수정/삭제 가능
- **좋아요 기능**: 다른 사람이 작성한 글에 좋아요, 싫어요 투표 가능
- **댓글 기능**: 
  - 대댓글 지원
  - 본인이 작성한 댓글/대댓글만 삭제 가능

---

###  관리자 기능

- **대시보드**: 회원 수, 게시글 수, 댓글 수
- **회원 목록 관리**: 회원 전체 조회 및 삭제
- **게시글 목록 관리**: 게시글 전체 조회 및 삭제
- **댓글 목록 관리**: 댓글 전체 조회 및 삭제

---

##  프로젝트 구조

```plaintext
src
 └─ main
     ├─ java
     │   └─ com.example.start
     │       ├─ controller       # 요청 처리 컨트롤러 (AdminController, UserController, PostController, CommentController)
     │       ├─ dto              # DTO 객체 (PostForm, CommentForm)
     │       ├─ entity           # JPA 엔티티 (User, Post, Comment)
     │       ├─ repository       # 데이터 접근 레이어
     │       ├─ service          # 서비스 인터페이스
     │       └─ service.impl     # 서비스 구현체
     └─ resources
         ├─ templates
         │   ├─ admin
         │   │   ├─ dashboard.html
         │   │   ├─ user-list.html
         │   │   ├─ post-list.html
         │   │   └─ comment-list.html
         │   ├─ login.html
         │   ├─ main.html
         │   ├─ post-detail.html
         │   ├─ post-edit.html
         │   ├─ post-form.html
         │   ├─ post-list.html
         │   ├─ signup.html
         │   └─ user-info.html
         └─ application.properties

실행 방법
1. MySQL에서 boarddb 데이터베이스 생성
2. application.properties에서 DB 연결 정보 확인
3. 프로젝트 빌드 및 실행

# macOS / Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run

4. 브라우저에서 http://localhost:8080 접속

시연 예시
로그인 → 글쓰기 → 댓글 → 관리자 로그인 → 회원/게시글/댓글 관리

