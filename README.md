# Admin Project

> 복잡한 조회·집계·인증 시나리오를 직접 구현해보기 위해 만든 관리자(Back-office) API 프로젝트입니다.  
Querydsl을 활용한 동적 검색/집계, 커서 기반 페이징, Redis 캐싱, SSE 실시간 알림,  
Spring Security 기반 REST 로그인 및 OAuth2 소셜 로그인 흐름 등  
실무에서 자주 등장하지만 깊이 다뤄보기 어려웠던 기능들을 중심으로 설계·구현했습니다.

[![Java](https://img.shields.io/badge/Java-17-red)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)]()
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)]()
[![Redis](https://img.shields.io/badge/Redis-latest-red)]()

---

## 📋 목차

- [프로젝트 소개](#-프로젝트-소개)
- [기술 스택](#-기술-스택)
- [주요 기능](#-주요-기능)
- [핵심 구현 사항](#-핵심-구현-사항)
  - [1. 커서 기반 페이징](#1️⃣-커서-기반-페이징-cursor-pagination)
  - [2. Redis 캐싱 성능 개선](#2️⃣-redis-캐싱-성능-개선)
  - [3. 실시간 알림 시스템](#3️⃣-실시간-알림-시스템-sse)
  - [4. Spring Security 인증](#4️⃣-spring-security-커스텀-인증)
  - [5. 통계 집계 시스템](#5️⃣-신규-가입자-통계-시스템)
- [트러블 슈팅](#-트러블-슈팅)

---

## 🎯 프로젝트 소개

### 개요
관리자를 위한 대규모 사용자 관리 시스템으로, 효율적인 검색/조회와 실시간 알림 기능을 제공합니다.

**주요 특징**
- 대용량 데이터 효율적 조회 (커서 페이징)
- 빠른 응답 속도 (Redis 캐싱)
- 실시간 사용자 경험 (SSE 알림)
- 안전한 인증/인가 (Spring Security + OAuth2)

### ERD & 아키텍처
| DB                                                                                    | 도메인                                                                                     |
|---------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| ![DB](https://github.com/user-attachments/assets/0383b0ba-6920-4f45-858c-919c0db4d105) | ![도메인](https://github.com/user-attachments/assets/ae6d69e7-eba0-42e0-ad02-a9fe4f27d7c4) |

---

## 🛠 기술 스택

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3.x, Spring Security
- **ORM**: Spring Data JPA
- **Authentication**: Spring Security, OAuth

### Database & Cache
- **Database**: MySQL 8.0, H2 (Test)
- **Cache**: Redis

### Tools & Libraries
- **Build**: Gradle
- **Testing**: JUnit 5, JMeter
- **API Docs**: Swagger

---

## 💡 주요 기능

### 1. 사용자 관리
- 동적 검색 (이메일, 닉네임, 역할, 가입일 등 복합 조건)
- 커서 기반 페이징으로 안정적인 대용량 데이터 조회
- 정렬 기준 변경 가능 (가입일/이메일 기준)

### 2. 인증/인가
- REST API 기반 로그인
- OAuth2 소셜 로그인 (Google, Naver)
- 역할 기반 접근 제어 (ROLE_USER, ROLE_ADMIN)
- 회원가입 후 자동 로그인

### 3. 실시간 알림
- SSE 기반 실시간 푸시 알림
- DB 기반 알림 히스토리 관리
- 미읽음 알림 조회 및 읽음 처리

### 4. 통계 및 분석
- 기간별 신규 가입자 통계 (일/주/월/년)
- 전 기간 대비 성장률 계산
- 최대/최소 구간 분석

---

## 🎨 핵심 구현 사항

## 1️⃣ 커서 기반 페이징 (Cursor Pagination)

### 📌 문제 인식
- 기본 Offset 페이징의 성능 저하 (페이지가 깊어질수록 느려짐)
- 데이터 변경 시 중복/누락 문제 발생
- 대규모 사용자 데이터에서 일정한 성능 보장 필요

### ✅ 해결 방법
**커서 기반 페이징 + Querydsl 동적 쿼리**로 안정적이고 빠른 조회 구현

#### 핵심 전략
1. **유니크 커서 생성**: 정렬 기준에 따라 고유한 커서 ID 생성
  - 가입일 정렬: `[가입일 20자리] + [userId 10자리]`
  - 이메일 정렬: 이메일 자체를 커서로 사용

2. **size + 1 조회**: 요청 사이즈보다 1개 더 조회하여 다음 페이지 존재 여부 판단

3. **동적 쿼리**: BooleanBuilder로 조건이 없는 경우 자동 무시

#### 구현 예시
```java
// Repository - 커서 조건 생성
private BooleanExpression getCursorCondition(String cursorId, String sortType, String order) {
    if (cursorId == null || cursorId.equals("0")) {
        return null;
    }
    
    if (sortType.equals("CREATED_AT")) {
        // 가입일 + userId 조합 커서
        String compositeKey = extractCompositeKey(cursorId);
        return order.equals("DESC") 
            ? user.compositeKey.lt(compositeKey)
            : user.compositeKey.gt(compositeKey);
    } else {
        // 이메일 커서
        return order.equals("DESC")
            ? user.email.lt(cursorId)
            : user.email.gt(cursorId);
    }
}

// Service - 다음 페이지 판단
if (users.size() > condition.getSize()) {
    hasNext = true;
    User lastUser = users.get(condition.getSize() - 1);
    nextCursorId = generateCursor(lastUser, sortType);
    users = users.subList(0, condition.getSize());
}
```

### 📊 주요 기능
- 다중 필터 지원 (이메일, 닉네임, 역할, 제공자, 가입일 범위)
- 정렬 기준 변경 (가입일/이메일 오름차순/내림차순)
- 페이지 이동 시 중복/누락 없는 안정적인 데이터 조회

### 🔗 관련 코드
- [검색 조건 DTO](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/admin/dto/request/AdminUserSearchCondition.java)
- [Repository 구현](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/user/repository/query/UserRepositoryImpl.java#L91)
- [Service 로직](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/admin/service/AdminUserService.java#L187)

---

## 2️⃣ Redis 캐싱 성능 개선

### 📌 문제 인식
- 사용자 프로필 조회가 전체 API 호출의 상당 부분 차지
- 빈번한 DB 조회로 인한 평균 응답시간 1.7초
- 변경 빈도는 낮지만 조회 빈도가 높은 데이터 특성

### ✅ 해결 방법
**Redis + Spring Cache**를 활용한 캐싱 전략 적용

#### 캐싱 전략
- **조회**: `@Cacheable` - Cache Aside 패턴
- **수정**: `@CacheEvict` - 데이터 수정 시 캐시 무효화
- **Key 전략**: `users:{userId}:profile`
- **TTL**: 설정에 따라 조정 가능

#### 구현 예시
```java
// 조회 - 캐시 적용
@Cacheable(
    cacheNames = "userCache",
    key = "'users:' + #id + ':profile'",
    cacheManager = "cacheManager"
)
@Transactional(readOnly = true)
public UserResponse getUserById(Long id) {
    log.debug("Cache Miss for user ID: {}", id);
    User findUser = userRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    return UserResponse.from(findUser);
}

// 수정 - 캐시 무효화
@CacheEvict(
    cacheNames = "userCache",
    key = "'users:' + #principal.getId() + ':profile'"
)
@Transactional
public UserResponse updateUser(UserPrincipal principal, UserUpdateRequest request) {
    log.debug("Cache Evict for user ID: {}", principal.getId());
    // 사용자 정보 업데이트 로직
}
```

### 📊 성능 측정 결과 (JMeter)

#### 테스트 환경
- 100명의 가상 사용자(Threads)
- 동일한 사용자 프로필을 10회 조회

#### 측정 결과
| 지표 | 캐시 적용 전 | 캐시 적용 후 | 개선율 |
|------|-------------|-------------|--------|
| 평균 응답시간 | 1,789ms | 651ms | **-63.61%** |
| 최소 응답시간 | 51ms | 25ms | **-50.98%** |
| 최대 응답시간 | 8,782ms | 4,164ms | **-52.58%** |
| 표준 편차 | 1,493.38ms | 726.83ms | **-51.33%** |
| 처리량 | 37.99 req/s | 74.96 req/s | **+97.32%** |

#### 비교 그래프
| 캐시 적용 전 | 캐시 적용 후 |
|--------------|--------------|
| ![before](https://github.com/user-attachments/assets/1229b17a-1a28-4b2b-83e7-5afd6189f0de) | ![after](https://github.com/user-attachments/assets/4bd1b499-6195-486d-a5cc-337a14de0c26) |

### 🔗 관련 코드
- [캐시 적용](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/user/service/UserService.java#L50)
- [캐시 무효화](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/user/service/UserService.java#L66)

---

## 3️⃣ 실시간 알림 시스템 (SSE)

### 📌 구현 목표
- 댓글/이벤트 발생 시 실시간 알림 전송
- 연결이 끊겨도 알림 누락 방지
- 미읽음 알림 관리 기능 제공

### ✅ 해결 방법
**SSE(Server-Sent Events) + DB 히스토리** 하이브리드 방식

#### 아키텍처
```
이벤트 발생
    ↓
DB에 알림 저장 (영속성 보장)
    ↓
사용자 SSE 연결 확인
    ↓
┌─────────────┬──────────────┐
│ 연결 O      │ 연결 X       │
│ 실시간 전송 │ DB에만 저장  │
└─────────────┴──────────────┘
         ↓
사용자 재접속 시 미읽음 알림 조회 API 호출
```

#### 핵심 구현
```java
// 알림 생성 및 전송
@Transactional
public void sendNotification(User user, String message, NotificationType type,
                             EntityType entityType, Long entityId) {
    // 1. DB 저장 (영속성 보장)
    Notification notification = Notification.createNotification(
        user, message, type, entityType, entityId
    );
    Notification saved = notificationRepository.save(notification);
    
    // 2. 실시간 전송 시도 (구독 중인 경우에만)
    sendRealTimeNotification(saved);
}

// 실시간 전송
private void sendRealTimeNotification(Notification notification) {
    SseEmitter emitter = userEmitters.get(notification.getTarget().getId());
    if (emitter != null) {
        try {
            emitter.send(SseEmitter.event()
                .name("notification")
                .data(convertToResponse(notification)));
        } catch (Exception e) {
            userEmitters.remove(notification.getTarget().getId());
        }
    }
}
```

### 📡 API 엔드포인트
- `GET /api/v1/notifications/stream` - SSE 구독 (실시간 스트림)
- `GET /api/v1/notifications/unread` - 미읽음 알림 조회
- `PATCH /api/v1/notifications/{id}/read` - 개별 읽음 처리
- `PATCH /api/v1/notifications/read-all` - 전체 읽음 처리

### 🔒 안정성 처리
- 연결 타임아웃/에러 시 자동 emitter 정리
- 연결 직후 더미 이벤트로 연결 확인
- DB 기반 히스토리로 알림 누락 방지
- 재연결 시 미읽음 알림 조회 가능

### 🔗 관련 코드
- [SSE 연결 관리](http://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/notification/service/NotificationService.java#L36)
- [알림 전송](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/notification/service/NotificationService.java#L90)
- [Entity 구조](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/notification/entity/Notification.java)
- [API 엔드포인트](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/notification/controller/NotificationController.java)
---

## 4️⃣ Spring Security 커스텀 인증

### 📌 구현 목표
- REST API 기반 JSON 로그인
- OAuth2 소셜 로그인 (Google, Naver)
- 역할 기반 접근 제어
- 회원가입 후 자동 로그인

### ✅ 인증 흐름

#### REST 로그인
```
클라이언트
    ↓ POST /api/v1/auth/login (JSON)
RestAuthenticationFilter (요청 가로채기)
    ↓ LoginRequest 파싱
RestAuthenticationToken 생성
    ↓
AuthenticationManager
    ↓
RestAuthenticationProvider (비밀번호 검증)
    ↓
┌──────────────┬─────────────┐
│ 성공         │ 실패        │
│ SuccessHandler │ FailureHandler │
└──────────────┴─────────────┘
```

#### OAuth2 로그인
```
소셜 로그인 요청
    ↓
OAuth2 Provider (Google/Naver)
    ↓
CustomOAuth2UserService (사용자 정보 로드)
    ↓
DB 저장/업데이트
    ↓
┌──────────────┬─────────────┐
│ 추가 정보 필요 │ 완료       │
│ /additional-info │ /       │
└──────────────┴─────────────┘
```

### 🔐 보안 설정
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/v1/auth/login", "/api/v1/auth/signup").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(new RestAuthenticationEntryPoint())
            .accessDeniedHandler(new RestAccessDeniedHandler())
        );
    
    return http.build();
}
```

### 🎯 주요 기능
- **커스텀 필터**: `RestAuthenticationFilter`로 JSON 기반 로그인 처리
- **자동 로그인**: 회원가입 후 `AuthenticationManager`로 자동 인증 [코드 보기](https://github.com/ijnooyah/admin-project/blob/8afacd2a9105107c847f7d4205b3f46cff2e67a9/src/main/java/com/yoonji/adminproject/user/controller/AuthController.java#L48)
- **소셜 로그인**: `CustomOAuth2UserService`로 사용자 정보 로드 및 저장 [코드 보기](https://github.com/ijnooyah/admin-project/blob/8afacd2a9105107c847f7d4205b3f46cff2e67a9/src/main/java/com/yoonji/adminproject/security/service/CustomOAuth2UserService.java#L45)
- **예외 처리**: 401/403 커스텀 응답

### 🔗 관련 코드
- [SecurityConfig](https://github.com/ijnooyah/admin-project/blob/8afacd2a9105107c847f7d4205b3f46cff2e67a9/src/main/java/com/yoonji/adminproject/security/config/SecurityConfig.java)
- [RestAuthenticationFilter](https://github.com/ijnooyah/admin-project/blob/8afacd2a9105107c847f7d4205b3f46cff2e67a9/src/main/java/com/yoonji/adminproject/security/filter/RestAuthenticationFilter.java#L47)
- [RestAuthenticationProvider](https://github.com/ijnooyah/admin-project/blob/8afacd2a9105107c847f7d4205b3f46cff2e67a9/src/main/java/com/yoonji/adminproject/security/provider/RestAuthenticationProvider.java#L29)

---

## 5️⃣ 신규 가입자 통계 시스템

### 📌 구현 목표
- 기간별 신규 가입자 추이 분석 (일/주/월/년)
- 전 기간 대비 성장률 계산
- 관리자 대시보드용 통계 제공

### ✅ 해결 방법
**Querydsl 집계 + 서비스 레이어 후처리**

#### 처리 흐름
```
1. Repository (Querydsl)
   - 기간별 GROUP BY로 신규 가입자 수 집계
   - DB 레벨에서 날짜 포맷팅 및 그룹핑
   
2. Service Layer
   - 전 기간 대비 성장률 계산
   - 전체/평균/최대/최소 구간 산출
   
3. Response
   - 통계 리스트 + 요약 지표 반환
```

#### 핵심 구현
```java
// Repository - Querydsl 집계
@Override
public List<PeriodStatistics> getNewUserStatistics(
    String timeUnit, LocalDate startDate, LocalDate endDate
) {
    StringTemplate dateFormat = getDateFormat(timeUnit);
    
    return queryFactory
        .select(new QPeriodStatistics(
            dateFormat.as("period"),
            user.id.count().intValue().as("newUsers")
        ))
        .from(user)
        .where(
            user.createdAt.between(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
            ).and(isNotDeleted())
        )
        .groupBy(dateFormat)
        .orderBy(dateFormat.asc())
        .fetch();
}

// Service - 성장률 계산
private void calculateGrowthRates(List<PeriodStatistics> stats) {
    IntStream.range(1, stats.size()).forEach(i -> {
        var current = stats.get(i);
        var previous = stats.get(i - 1);
        
        double growthRate = (current.getNewUsers() - previous.getNewUsers())
            / (double) previous.getNewUsers() * 100;
        
        current.setGrowthRate(Math.round(growthRate * 100.0) / 100.0);
    });
    
    stats.get(0).setGrowthRate(null); // 첫 기간은 비교 대상 없음
}
```

### 📊 API 사용 예시
```
GET /api/v1/admin/stats/new?timeUnit=day&startDate=2024-01-01&endDate=2024-01-31

Response:
{
  "timeUnit": "day",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "totalNewUsers": 1250,
  "averageNewUsersPerPeriod": 40.32,
  "statistics": [
    {
      "period": "2024-01-01",
      "newUsers": 35,
      "growthRate": null
    },
    {
      "period": "2024-01-02",
      "newUsers": 42,
      "growthRate": 20.0
    },
    ...
  ],
  "maxNewUsers": {
    "period": "2024-01-15",
    "newUsers": 68
  },
  "minNewUsers": {
    "period": "2024-01-07",
    "newUsers": 22
  }
}
```

### 🎯 주요 기능
- 시간 단위별 유연한 집계 (day/week/month/year)
- DB 레벨 집계로 효율적인 성능
- 성장률 자동 계산
- 피크 구간 자동 탐지

### 🔗 관련 코드
- [Repository 구현](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/user/repository/query/UserRepositoryImpl.java#L53)
- [Service 로직](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/admin/service/AdminUserService.java#L231)
- [Response DTO](https://github.com/ijnooyah/admin-project/blob/master/src/main/java/com/yoonji/adminproject/admin/dto/response/NewUserStatisticsResponse.java)

---

## 🐛 트러블 슈팅

### 1. Fetch Join + Pagination 메모리 페이징 문제

#### 문제 상황
```java
// 문제 코드
List<User> users = queryFactory
    .selectFrom(user)
    .leftJoin(user.roles, role).fetchJoin()
    .offset(page * size)
    .limit(size)
    .fetch();
```
- Hibernate 경고: `firstResult/maxResults specified with collection fetch; applying in memory`
- LIMIT이 DB가 아닌 메모리에서 적용됨
- 모든 데이터를 조회한 후 애플리케이션에서 페이징 처리

#### 원인 분석
Hibernate는 컬렉션 Fetch Join과 LIMIT를 동시에 사용할 경우:
1. 모든 데이터를 DB에서 조회
2. 애플리케이션 메모리에서 중복 제거 (distinct)
3. 메모리에서 페이징 처리

→ **대용량 데이터에서 OOM(Out Of Memory) 위험**

#### 해결 방법
```java
// 개선 코드
List<User> users = queryFactory
    .selectFrom(user)
    .distinct()  // Fetch Join 제거, distinct 적용
    .where(conditions)
    .offset(page * size)
    .limit(size)
    .fetch();
```

**적용한 해결책**
1. **Fetch Join 제거**: 컬렉션 조인 제거
2. **distinct() 적용**: 사용자 중복 제거
3. **Batch Fetch Size 설정**: `hibernate.default_batch_fetch_size=100`
  - N+1 문제 해결
  - DB 레벨 페이징 유지

#### 결과
- ✅ DB 레벨 LIMIT 정상 작동
- ✅ 메모리 사용량 최소화
- ✅ N+1 문제 해결 (Batch Fetch)
- ✅ 대용량 데이터 안정적 처리

#### 학습 내용
> 📝 [상세 분석 블로그 포스트](https://ijnooyah.github.io/querydsl/fetch-join-limit)

**핵심 교훈**
- 컬렉션 Fetch Join + 페이징은 함께 사용하지 말 것
- Batch Fetch Size가 대부분의 경우 더 나은 선택
- 
---

### 2. 통계 계산 시 0으로 나누기 오류 (예정)

#### 문제 상황
```java
// 잠재적 문제 코드
double growthRate = (current - previous) / (double) previous * 100;
// previous가 0이면 ArithmeticException 발생

```
#### 해결 예정
- 성장률 계산 전 방어 코드 추가
- `previous == 0`인 경우 별도 처리 (예: null 또는 "N/A")

