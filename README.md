# kotlin-board-8

> Spring Framework 없이 코어 기능을 직접 구현하여 CRUD 기능을 개발하는 프로젝트입니다. 웹 서버를 직접 구축하며, 프레임워크의 동작 원리를 이해하는 것을 목표로 합니다.

# 기술스택

- 언어: Kotlin
- HTTP 서버: com.sun.net.httpserver.HttpServer (JDK 내장)
- 데이터베이스: JPA + Hibernate + HikariCP
- JSON 처리: Jackson ObjectMapper

---

# 기능 요구사항

## 1. HTTP 서버 구현

### 1.1 기본 서버 실행

- [X] com.sun.net.httpserver.HttpServer를 사용하여 HTTP 서버 시작
- [ ] 포트 번호 설정 가능
- [X] 서버 시작/종료 로직 구현

### 1.2 요청/응답 처리

- [X] HTTP 요청 수신 (GET, POST, PUT, DELETE)
- [X] HTTP 응답 생성 (상태 코드, 헤더, 바디)
- [X] Content-Type: application/json 지원

## 2. 라우팅 시스템

### 2.1 기본 라우팅

- [X] URL 패턴과 핸들러 함수 매핑 (예: `/boards` → `handleGetBoards()`)
- [X] HTTP 메서드별 라우팅 (GET, POST, PUT, DELETE)
- [ ] Path Variable 지원 (예: `/boards/{id}`)

### 2.2 라우팅 등록

- [X] 수동 라우트 등록 API 제공

```kotlin
router.get("/boards") { request -> handleGetBoards(request) }
router.post("/boards") { request -> handleCreateBoard(request) }
router.get("/boards/{id}") { request -> handleGetBoard(request) }
router.put("/boards/{id}") { request -> handleUpdateBoard(request) }
router.delete("/boards/{id}") { request -> handleDeleteBoard(request) }
```

## 3. JSON 처리

### 3.1 직렬화/역직렬화

- [X] ObjectMapper를 사용한 객체 → JSON 변환
- [ ] ObjectMapper를 사용한 JSON → 객체 변환
- [ ] 요청 바디를 자동으로 Kotlin 객체로 변환
- [X] 응답 객체를 자동으로 JSON으로 변환

## 4. 데이터베이스 연동

### 4.1 데이터 소스 설정

- [ ] HikariCP를 사용한 커넥션 풀 구성
- [ ] 데이터베이스 연결 정보 설정

### 4.2 JPA/Hibernate 설정

- [ ] EntityManagerFactory 생성
- [ ] EntityManager 관리
- [ ] 기본 CRUD 작업 지원
    - [ ] 엔티티 저장 (persist)
    - [ ] 엔티티 조회 (find)
    - [ ] 엔티티 수정 (merge)
    - [ ] 엔티티 삭제 (remove)
    - [ ] JPQL 쿼리 실행

### 4.3 트랜잭션 관리

- [ ] 수동 트랜잭션 시작/커밋/롤백

```kotlin
entityManager.transaction.begin()
try {
    entityManager.transaction.commit()
} catch (e: Exception) {
    entityManager.transaction.rollback()
}
```

## 5. 의존성 주입 (DI)

### 5.1 수동 DI

- [ ] Controller, Service, Repository 계층 분리
- [ ] 생성자를 통한 수동 의존성 주입

```kotlin
val repository = BoardRepository(entityManager)
val service = BoardService(repository)
val controller = BoardController(service)
```

## 6. 예외 처리

### 6.1 기본 예외 핸들링

- [X] 애플리케이션 예외를 HTTP 응답으로 변환
- [X] 적절한 HTTP 상태 코드 반환
- [X] 에러 응답 JSON 형식 통일

```json
{
  "error": "ResourceNotFound",
  "message": "Board with id 123 not found"
}
```

---

# 구현 목표

## 최종 목표: 간단한 Board CRUD API 구현

### API 엔드포인트

- [ ] `GET /boards` - 전체 게시글 목록 조회
- [ ] `GET /boards/{id}` - 특정 게시글 상세 조회
- [ ] `POST /boards` - 새 게시글 생성
- [ ] `PUT /boards/{id}` - 게시글 정보 수정
- [ ] `DELETE /boards/{id}` - 게시글 삭제

### Board 엔티티

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

# 성공 기준

프로젝트는 다음 조건을 만족하면 성공으로 간주한다:

1. HTTP 서버가 정상적으로 시작되고 요청을 받을 수 있다
2. Board CRUD API 5개가 모두 정상 동작한다
3. 데이터베이스에 게시글이 정상적으로 저장/조회/수정/삭제된다
4. JSON 요청/응답이 정상적으로 처리된다
5. 예외 상황에서 적절한 에러 응답을 반환한다
6. 코드가 계층별로 분리되어 있고, 각 계층의 역할을 명확히 수행한다 (Controller/Service/Repository)

---

# 테스트 시나리오

## 시나리오 1: 게시글 생성 및 조회

1. 게시글 생성
    - `POST /boards`로 새 게시글 생성
    - 요청 바디: `{"title": "첫 게시글", "content": "안녕하세요", "author": "홍길동"}`
    - 예상 응답: `201 Created`, 생성된 게시글 정보 (id 포함)

2. 단일 게시글 조회
    - `GET /boards/{id}`로 생성한 게시글 조회
    - 예상 응답: `200 OK`, 생성 시 입력한 정보와 동일한 게시글 반환

3. 전체 게시글 목록 조회
    - `GET /boards`로 목록 조회
    - 예상 응답: `200 OK`, 생성한 게시글이 목록에 포함

## 시나리오 2: 게시글 수정

1. 게시글 수정
    - `PUT /boards/{id}`로 제목과 내용 수정
    - 요청 바디: `{"title": "수정된 제목", "content": "수정된 내용", "author": "홍길동"}`
    - 예상 응답: `200 OK`, 수정된 게시글 정보

2. 수정 확인
    - `GET /boards/{id}`로 조회
    - 예상 결과: 제목과 내용이 수정된 내용으로 변경됨

## 시나리오 3: 게시글 삭제

1. 게시글 삭제
    - `DELETE /boards/{id}`로 게시글 삭제
    - 예상 응답: `204 No Content` 또는 `200 OK`

2. 삭제 확인
    - `GET /boards/{id}`로 조회 시도
    - 예상 응답: `404 Not Found`

3. 목록에서 제외 확인
    - `GET /boards`로 목록 조회
    - 예상 결과: 삭제된 게시글이 목록에 없음

## 시나리오 4: 예외 처리

1. 존재하지 않는 게시글 조회
    - `GET /boards/99999`
    - 예상 응답: `404 Not Found`
    - 예상 바디: `{"error": "ResourceNotFound", "message": "Board with id 99999 not found"}`

2. 잘못된 요청 바디
    - `POST /boards`에 필수 필드 누락
    - 예상 응답: `400 Bad Request`

3. 존재하지 않는 게시글 수정 시도
    - `PUT /boards/99999`
    - 예상 응답: `404 Not Found`
