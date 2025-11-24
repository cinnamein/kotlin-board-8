# kotlin-board-8

> Spring boot의 도움 없이, 웹 서버부터 DI·라우팅·트랜잭션까지 핵심 구조를 Java만을 사용해 구현하여 프레임워크의 내부 구조를 학습한 CRUD 프로젝트입니다.

프로젝트 회고록은 [📍여기](https://velog.io/@cinnamein/우테코-프리코스-오픈미션-스프링-클론코딩)에서 확인할 수 있습니다.

---

# 실행 방법

## 서버 실행

터미널에 아래 명령어를 입력해주세요.

```bash
./gradlew bootjar
```

서버가 정상적으로 시작되면 다음과 같은 로그가 출력됩니다:

```
09:33:33.228 [main] INFO web.server.WebServer -- Server started on http://localhost:8080
<============-> 93% EXECUTING [1s]
```

이후 [localhost:8080](http://localhost:8080)으로 접근하면 UI를 통해 테스트가 가능합니다.

## API 엔드포인트
- `GET /boards` - 전체 게시글 목록 조회
- `GET /boards/{id}` - 특정 게시글 상세 조회
- `POST /boards` - 새 게시글 생성
- `PUT /boards/{id}` - 게시글 정보 수정
- `DELETE /boards/{id}` - 게시글 삭제


## Board 엔티티

```kotlin
@Entity
@Table(name = "boards")
data class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    val title: String,
    val content: String,
    val author: String,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

---

# 기술스택

- 언어: Kotlin
- HTTP 서버: com.sun.net.httpserver.HttpServer (JDK 내장)
- 데이터베이스: H2 (In-Memory) + JPA + Hibernate
- 커넥션 풀: HikariCP
- JSON 처리: Jackson ObjectMapper

---

# 기능 요구사항

## 1. HTTP 서버

- [X] `com.sun.net.httpserver.HttpServer`를 사용한 서버 시작
- [X] HTTP 메서드별 요청 처리 (GET, POST, PUT, DELETE)
- [X] JSON 요청/응답 지원

## 2. 라우팅
- [X] URL 패턴 매칭
- [X] Path Variable 지원 (/boards/{id})
- [X] HTTP 메서드 구분

## 3. DI 컨테이너
- [X] 컴포넌트 스캔 및 빈 등록
- [X] 생성자 기반 의존성 주입
- [X] 인터페이스 자동 매핑
- [X] 순환 참조 감지

## 4. 데이터베이스

- [X] HikariCP 커넥션 풀
- [X] JPA/Hibernate 연동
- [X] 메모리 H2 데이터베이스 연결
- [X] 트랜잭션 관리
- [X] CRUD 작업

## 5. 예외 처리

- [X] HTTP 상태 코드 자동 매핑
- [X] 통일된 에러 응답 형식
