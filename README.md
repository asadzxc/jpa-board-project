프로젝트 소개 - JPA 게시판
이 프로젝트는 Spring Boot + JPA를 기반으로 한 간단한 게시판 웹 애플리케이션입니다.
주요 기능은 회원가입, 로그인, 로그아웃, 사용자 정보 페이지, 글쓰기 및 게시글 목록 출력 등입니다.

사용 기술

Java 17,
Spring Boot,
Spring Data JPA,
Thymeleaf,
MySQL,
Maven

기능 요약
회원가입: 이름, 아이디, 비밀번호 입력 후 가입

로그인: 아이디와 비밀번호로 로그인 처리

로그아웃: 세션을 통해 로그아웃 처리

메인 페이지:

로그인 상태일 경우: {사용자 이름}님 환영합니다! 표시 + 로그아웃 버튼

비로그인 상태일 경우: 로그인 / 회원가입 링크 표시

내 정보 페이지: 회원가입 직후 해당 회원의 정보 출력

게시글 기능 (신규)
글쓰기: 로그인한 사용자만 가능, 제목/내용 입력 후 등록

글 목록 페이지: 등록된 게시글 전체 조회 (ID / 제목 / 내용)

프로젝트 구조


src
 └─ main
     ├─ java
     │   └─ com.example.start
     │       ├─ controller       # 요청 처리 컨트롤러 (UserController, PostController)
     │       ├─ entity           # JPA 엔티티 (User, Post)
     │       ├─ repository       # 데이터 접근 레이어 (UserRepository, PostRepository)
     │       ├─ service          # 서비스 인터페이스
     │       └─ service.impl     # 서비스 구현체 (UserServiceImpl, PostServiceImpl)
     └─ resources
         ├─ templates            # Thymeleaf HTML 템플릿
         │   ├─ main.html
         │   ├─ login.html
         │   ├─ signup.html
         │   ├─ user-info.html
         │   ├─ post-form.html      # 글쓰기 폼
         │   └─ post-list.html      # 게시글 목록
         └─ application.properties # 환경설정 파일

* 실행 방법
MySQL에서 boarddb 데이터베이스 생성

application.properties에서 DB 설정 확인 (계정/비밀번호)

프로젝트 빌드 후 실행

./mvnw spring-boot:run
브라우저에서 http://localhost:8080 접속
