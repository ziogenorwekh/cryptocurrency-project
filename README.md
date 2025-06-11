# 암호화폐 거래소 클론 프로젝트

### **프로젝트 관리자**

### **프로젝트 Github 주소**

[GitHub - ziogenorwekh/cryptocurrency-project](https://github.com/ziogenorwekh/cryptocurrency-project/tree/dev)

### **프로젝트 기간**

2025.06 ~

# 🎯프로젝트 목표

---

<aside>
<img src="https://www.notion.so/icons/bookmark_gray.svg" alt="https://www.notion.so/icons/bookmark_gray.svg" width="40px" /> 암호화폐 거래를 제공하는 회사의 API를 활용하여 암호화폐 거래 및 AI 분석을 포함한 암호화폐 거래소 웹페이지 클론 프로젝트

</aside>

# 🏁프로젝트 결과

---

# 📎기술 스택

---

### **📌 백엔드 기술 스택**

- **프레임워크**: Spring Framework
- **언어**: Java
- **빌드 도구**: Gradle
- **데이터베이스**: MySQL(테스트 및 배포), H2Database(테스트)
- **아키텍처**: MSA(Microservices Architecture)
- **ORM(Object-Relational Mapping)**: JPA
- **서비스 간 통신**: **Apache Kafka** (비동기 메시징 브로커)
- **실시간 통신**: **WebSocket**

### **📌 인프라 & 배포**

- **컨테이너화**: Docker
- **컨테이너 오케스트레이션**: Kubernetes
- **구성 관리 & 자동화**: Jenkins (로컬 실행) / Ansible (원격 실행)
- **클라우드 서비스(AWS)**
    - EC2 (컴퓨팅), Route 53 (DNS 관리), RDS (데이터베이스 관리)

**모니터링 & 시각화**:

- **Prometheus**: 애플리케이션 및 시스템 메트릭 수집
- **Grafana**: Prometheus 데이터 시각화 대시보드

### **📌 프론트엔드 기술 스택**

- **프레임워크/라이브러리**: React
- **스타일링**: Bootstrap
- **HTTP 클라이언트**: Axios
- **인증**: Google Login, Naver Login
- **웹 서버/리버스 프록시**: Nginx
- **실시간 통신**: **WebSocket API 또는 라이브러리**

### 📌 **개발 방식**

- **Agile 방법론**
- **테스트 주도 개발(TDD) , 도메인 주도 개발(DDD)**
- **헥사고날 아키텍처(Ports & Adapters), 유스케이스 중심 설계**
- **단위 테스트**: JUnit, Mockito
- **통합 테스트**: Spring Test
- **성능/부하 테스트**: Apache JMeter (K8s 환경 부하 테스트 및 모니터링 연동 목적)

# 🧍요구사항

---

## 기능 명세서

<aside>
<img src="https://www.notion.so/icons/bookmark_gray.svg" alt="https://www.notion.so/icons/bookmark_gray.svg" width="40px" /> [1] **사용자 접속 및 로그인**
└─> 프론트엔드에서 Google OAuth 등으로 인증 요청
└─> 유저 서비스에서 인증 처리 및 JWT 발급

[2] **마켓 데이터 조회**
└─> 프론트엔드에서 시세/호가/체결 데이터 요청
└─> 거래 서비스 → 외부 거래소 API 연동 or 캐시/DB에서 조회
└─> 프론트에 최신 시세 정보 반환

[3] **AI 기반 시장 분석 결과 제공**
└─> 사용자가 거래 페이지 접근 시, 6시간마다 갱신된 AI 분석 결과 표시
└─> Market Data Collection & Analysis Service에서

- 외부 시세 수집

- OpenAI 호출

- 분석 결과 캐시 후 제공

[4] **자산 현황 및 거래 내역 확인**
└─> 프론트엔드에서 포트폴리오 조회 요청
└─> 포트폴리오 서비스에서 유저 잔고, 평가 손익 계산
└─> 유저 서비스에서 거래 내역 조합해 종합 응답

[5] **주문 기능 (매수/매도/예약주문)**
└─> 프론트에서 거래 요청 (주문하기/취소 등)
└─> 거래 서비스에서 주문 유효성 검사 및 처리
└─> 거래소 API 또는 시뮬레이션 엔진 연동
└─> 결과 기록 후 유저/포트폴리오 서비스에 반영

</aside>

## API 명세서

### 1. 유저 서비스 (User Service API)

| 기능 | HTTP Method | URI | 설명 | 요청 바디 예시 (요약) |
| --- | --- | --- | --- | --- |
| 이메일 인증 요청 | POST | /emails | 사용자 이메일로 인증 코드 발송 | `{ "email": "user@example.com" }` |
| 사용자 생성(회원가입) | POST | /users | 신규 사용자 등록 | `{ "email": "...", "password": "...", "nickname": "..." }` |
| 비밀번호 변경 | PUT | /users/{userId}/password | 사용자 비밀번호 변경 |  |
| 사용자 프로필 조회 | GET | /users/{userId} | 특정 사용자 프로필 조회 | - |
| 사용자 프로필 수정 | PUT | /users/{userId} | 사용자 프로필 정보 수정 | `{ "nickname": "...", "profileImageUrl": "..." }` |
| 거래내역 조회 | GET | /users/{userId}/transactions | 사용자 거래 내역 조회 | - |
| 2단계 인증 설정 | PUT | /users/{userId}/security | 2단계 인증 설정 또는 해제 | `{ "enable2FA": true, "secret": "..." }` |
| 사용자 삭제 | DELETE | /users/{userId} | 사용자 계정 삭제 | - |
| 로그인 | POST | /login | 로그인 요청 및 JWT 토큰 발급 | `{ "email": "...", "password": "..." }` |

### 2. 거래 서비스 (Trading Service API)

| 기능 | HTTP Method | URI | 설명 | 요청 바디 예시 (요약) |
| --- | --- | --- | --- | --- |
| 시세 종목 조회 | GET | /market/items | 거래 가능한 종목 목록 조회 | - |
| 시세 캔들 데이터 조회 | GET | /market/items/{itemId}/candles | 특정 종목의 캔들 차트 데이터 조회 | 쿼리파라미터로 기간 지정 가능 |
| 시세 체결 내역 조회 | GET | /market/items/{itemId}/trades | 특정 종목의 최근 체결 내역 조회 | 쿼리파라미터로 limit, since 지정 가능 |
| 현재가 조회 | GET | /market/items/{itemId}/ticker | 특정 종목의 현재가 조회 | - |
| 호가 정보 조회 | GET | /market/items/{itemId}/orderbook | 특정 종목의 호가(매수/매도 주문서) 정보 조회 | - |
| 주문 목록 조회 | GET | /orders | 사용자 전체 주문 목록 조회 (필터 가능) | 쿼리파라미터(userId, status 등) |
| 특정 주문 조회 | GET | /orders/{orderId} | 주문 상세 정보 조회 | - |
| 주문 생성 (매수/매도) | POST | /orders | 신규 주문 생성 | `{ "userId": 1, "itemId": "BTC", "type": "buy", "price": 10000, "quantity": 0.1 }` |
| 주문 취소 | DELETE | /orders/{orderId} | 기존 주문 취소 | - |
| 예약 매수 주문 생성 | POST | /orders/reservations/buying | 예약 매수 주문 생성 | `{ "userId": 1, "itemId": "BTC", "price": 9500, "quantity": 0.1, "executeAt": "2025-06-10T10:00:00Z" }` |
| 예약 매도 주문 생성 | POST | /orders/reservations/selling | 예약 매도 주문 생성 | `{ "userId": 1, "itemId": "BTC", "price": 10500, "quantity": 0.1, "executeAt": "2025-06-10T10:00:00Z" }` |
| 예약 주문 목록 조회 | GET | /orders/reservations | 예약 주문 목록 조회 | 쿼리파라미터(userId 등) |
| 예약 주문 취소 | DELETE | /orders/reservations/{reservationId} | 예약 주문 취소 | - |
| 수수료 쿠폰 적용 | PUT | /orders/{orderId}/coupon | 특정 주문에 수수료 쿠폰 적용 | `{ "couponId": "abc123" }` |

### 3. 포트폴리오 서비스 (Portfolio Service API)

| 기능 | HTTP Method | URI | 설명 | 요청 바디 예시 (요약) |
| --- | --- | --- | --- | --- |
| 잔고 조회 | GET | /portfolio/{userId}/balance | 특정 사용자 자산 잔고 조회 | - |
| 자산 현황 조회 | GET | /portfolio/{userId}/assets | 특정 사용자 자산 현황(평가 금액 등) 조회 | - |
| 평가 손익 조회 | GET | /portfolio/{userId}/profitloss | 사용자 평가 손익 조회 | - |
| 입출금 내역 조회 | GET | /portfolio/{userId}/transactions | 입출금 내역 및 자산 변동 기록 조회 | - |
| 자산 변동 기록 생성 | POST | /portfolio/{userId}/transactions | 자산 변동 내역 생성 (입금, 출금 등) | `{ "type": "deposit", "amount": 1000, "timestamp": "..." }` |

### 4. 시장 데이터 수집 및 분석 서비스 (Market Data Collection & Analysis Service API)

| 기능 | HTTP Method | URI | 설명 | 요청 바디 예시 (요약) |
| --- | --- | --- | --- | --- |
| 외부 시세 데이터 수집/갱신 | POST | /marketdata/refresh | 외부 거래소 API로부터 시세 데이터 수집 및 갱신 | - |
| 시세 데이터 조회 | GET | /marketdata/items/{itemId} | 특정 종목의 시세 데이터 조회 | - |
| AI 분석 결과 조회 | GET | /marketdata/analysis | 최신 AI 기반 시장 분석 결과 조회 | - |
| AI 분석 결과 갱신 | POST | /marketdata/analysis/refresh | AI 분석 결과 6시간마다 갱신 (수동 호출 가능) | - |
| 캐시된 분석 결과 조회 | GET | /marketdata/analysis/cache | 캐시된 AI 분석 결과 조회 | - |

### 5. 쿠폰 서비스 (Coupon Service API)

| 기능 | HTTP Method | URI | 설명 | 요청 바디 예시 (요약) |
| --- | --- | --- | --- | --- |
| 쿠폰 발급 | POST | /coupons | 사용자 수수료 할인 쿠폰 발급 | `{ "userId": 1, "discount": 10, "validUntil": "2025-12-31" }` |
| 쿠폰 조회 | GET | /users/{userId}/coupons | 사용자 수수료 할인 쿠폰 목록 조회 | - |
| 쿠폰 삭제 | DELETE | /coupons/{couponId} | 특정 쿠폰 삭제 | - |

# 🛠️설계

---

## **📒헥사고날 아키텍처**

![image.png](readmeImg/image.png)

## 📒멀티 모듈

![image.png](readmeImg/image%201.png)

## 서비스별 동작 기능

### 1. 유저 서비스 (User Service)

- 회원가입, 로그인, 인증/인가 (JWT, OAuth 등)
- 프로필 관리
- 기본 거래내역 저장 및 조회 (간단히 기록)
- 비밀번호 재설정, 2단계 인증(선택사항)
- 쿠폰 관리

### 2. 거래 서비스 (Trading Service)

- 시세 종목 조회 (MarketItem)
- 시세 캔들, 체결, 현재가(Ticker), 호가(OrderBook) 조회
- 주문 처리: 주문 생성, 주문 취소
- 예약매수, 예약매도 등 주문 예약 기능
- 주문 상태 관리 및 거래 체결 처리

### 3. 포트폴리오 서비스 (Portfolio Service)

- 유저별 자산 현황, 잔고 계산
- 평가손익 관리 (실시간 혹은 주기적)
- 입출금 내역 통합 관리 (간단 버전)
- 자산 변동 히스토리 기록 및 조회

### 4. 시장 데이터 수집 및 분석 서비스 (Market Data Collection & Analysis Service)

- 외부 거래소 API로부터 시세 및 거래 데이터 수집
- 데이터 정제, 캐싱 및 저장
- 시세 데이터 API 제공 (거래 서비스와 분리하여 부하 분산)
- 데이터 갱신 주기 관리
- AI를 활용하여 추세 또는 예측

**쿠폰 서비스 (Coupon Service)**

- 수수료 할인 쿠폰 관리 (발급, 조회, 삭제)
- 쿠폰 적용 및 유효성 검사

## 서비스별 도메인 엔티티

- **유저 서비스 (User Service)**
    - **User**: 사용자 계정 정보 (ID, 이메일, 비밀번호, 프로필, 권한 등)
    - **Role**: 사용자 권한 정보 (관리자, 유저)
    - **SecuritySettings**: 2단계 인증 설정 정보 (활성화 여부, 인증 방법 등)
    - **TransactionHistory**: 유저의 기본 거래 내역 기록 (간단한 주문 기록)

- **시장 데이터 수집 및 분석 서비스 (Market Data Collection & Analysis Service)**
    - **MarketData**: 외부 API에서 수집한 시세 데이터 (종목별 시세, 체결, 호가 등)
    - **CandleStick**: 시세 캔들 정보 (시간대별 시가, 종가, 고가, 저가, 거래량)
    - **AIAnalysisResult**: AI 기반 시장 분석 결과 (예측, 트렌드, 신호 등)
    - **DataRefreshSchedule**: 데이터 갱신 주기 및 상태 정보

- **거래 서비스 (Trading Service)**
    - **MarketItem**: 거래 가능한 암호화폐 종목 정보 (심볼, 이름, 기본 단위 등)
    - **Order**: 주문 정보 (주문 ID, 유저 ID, 종목, 주문 타입(buy/sell), 수량, 가격, 상태)
    - **OrderBook**: 호가 정보 (매수/매도 리스트, 가격과 수량)
    - **TradeExecution**: 체결된 거래 내역 (체결 ID, 주문 ID, 체결 가격, 수량, 시간)
    - **ReservationOrder**: 예약 주문 정보 (예약 시간, 주문 상세)

- **포트폴리오 서비스 (Portfolio Service)**
    - **Portfolio**: 유저별 자산 현황 (각 암호화폐별 보유 수량, 총 평가 금액)
    - **Balance**: 유저의 잔고 (총 자산, 현금 잔고 등)
    - **ProfitLoss**: 평가 손익 내역 (실현/미실현 손익)
    - **DepositWithdrawal**: 입출금 기록 (금액, 일시, 유형)
    - **AssetChangeLog**: 자산 변동 기록 (변동 원인, 일시, 변동 금액)
- **쿠폰 서비스 (Coupon Service)**
    - **Coupon**: 수수료 할인 쿠폰 정보 (쿠폰 코드, 할인율, 만료일, 사용자 ID 등)

## 서비스별 도메인 엔티티 관계

- **유저 서비스 (User Service)**
    - **User 1 : 1 SecuritySettings**
        - 유저 당 2단계 인증 설정 정보 1개
    - **User 1 : N TransactionHistory**
        - 유저는 여러 거래 기록을 가질 수 있음
- **쿠폰 서비스 (Coupon Service)**
    - **User 1 : N Coupon**
        - 유저별로 여러 개의 쿠폰 발급 가능


- **시장 데이터 수집 및 분석 서비스 (Market Data Collection & Analysis Service)**
    - **MarketItem 1 : N MarketData**
        - 하나의 종목에 여러 시세 데이터 존재
    - **MarketItem 1 : N CandleStick**
        - 하나 종목에 여러 시간대별 캔들스틱 데이터
    - **MarketData 1 : 1 AIAnalysisResult**
        - 시세 데이터 하나에 대응하는 AI 분석 결과 존재 (또는 주기별 분석 결과와 매칭)
    - **DataRefreshSchedule 1 : N MarketData**
        - 데이터 갱신 일정에 따라 여러 시세 데이터 갱신

- **거래 서비스 (Trading Service)**
    - **User 1 : N Order**
        - 한 유저는 여러 주문(매수/매도)을 낼 수 있음
    - **Order 1 : N TradeExecution**
        - 한 주문이 여러 번 부분 체결될 수 있음
    - **OrderBook 1 : N Order**
        - 호가별로 여러 주문이 쌓임 (매수 호가, 매도 호가 각각 리스트)
    - **ReservationOrder 1 : 1 Order**
        - 예약 주문은 실제 주문과 1:1 대응

- **포트폴리오 서비스 (Portfolio Service)**
    - **User 1 : 1 Portfolio**
        - 유저마다 하나의 포트폴리오 보유
    - **Portfolio 1 : N Balance**
        - 포트폴리오 내 여러 암호화폐 잔고
    - **Portfolio 1 : N ProfitLoss**
        - 포트폴리오 평가 손익 기록 여러 개
    - **User 1 : N DepositWithdrawal**
        - 유저가 여러 입출금 기록 가짐
    - **Portfolio 1 : N AssetChangeLog**
        - 포트폴리오 내 자산 변동 로그 여러 개

# 💡구현

---

# 🚀배포 및 운영

---

# 🚧 트러블슈팅

---