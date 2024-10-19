### API 목록

#### 판매자 회원 가입

요청
- 메서드: POST
- 경로: /seller/signUp
- 본문
  ```
  CreateSellerCommand {
    email: string,
    username: string,
    password: string
  }
  ```

응답
- 204 No Content

테스트
- [x] 올바르게 요청하면 204 No Content 상태코드를 반환한다
- [x] email 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다
- [x] email 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다
- [x] username 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다
- [x] username 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다
- [x] password 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다
- [x] password 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다
- [x] email 속성에 이미 존재하는 이메일 주소가 지정되면 400 Bad Request 상태코드를 반환한다
- [x] username 속성에 이미 존재하는 사용자이름이 지정되면 400 Bad Request 상태코드를 반환한다
- [x] 비밀번호를 올바르게 암호화한다

#### 구매자 회원 가입

요청
- 메서드: POST
- 경로: /shopper/signUp
- 본문
  ```
  CreateShopperCommand {
    email: string,
    username: string,
    password: string
  }
  ```

응답
- 204 No Content

테스트
- [x] 올바르게 요청하면 204 No Content 상태코드를 반환한다
- [x] email 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다
- [x] email 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다
- [x] username 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다
- [x] username 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다
- [x] password 속성이 지정되지 않으면 400 Bad Request 상태코드를 반환한다
- [x] password 속성이 올바른 형식을 따르지 않으면 400 Bad Request 상태코드를 반환한다
- [x] email 속성에 이미 존재하는 이메일 주소가 지정되면 400 Bad Request 상태코드를 반환한다
- [x] username 속성이 이미 존재하는 사용자이름이 지정되면 400 Bad Request 상태코드를 반환한다
- [x] 비밀번호를 올바르게 암호화한다

#### 판매자 접근토큰 발행

요청
- 메서드: POST
- 경로: /seller/issueToken
- 본문
  ```
  IssueSellerToken {
    email: string,
    password: string
  }
  ```

응답
- 200 OK

  본문
  ```
  AccessTokenCarrier {
    accessToken: string
  }
  ```

테스트
- [x] 올바르게 요청하면 200 OK 상태코드를 반환한다
- [x] 올바르게 요청하면 접근토큰을 반환한다
- [x] 존재하지 않는 이메일이 사용되면 400 Bad Request 상태코드를 반환한다
- [x] 잘못된 비밀번호가 사용되면 400 Bad Request 상태코드를 반환한다

#### 구매자 접근토큰 발행

요청
- 메서드: POST
- 경로: /shopper/issueToken
- 본문
  ```
  IssueShopperToken {
    email: string,
    password: string
  }
  ```

응답
- 200 OK
- 본문
  ```
  AccessTokenCarrier {
    accessToken: string
  }
  ```

테스트
- [x] 올바르게 요청하면 200 OK 상태코드를 반환한다
- [x] 올바르게 요청하면 접근토큰을 반환한다
- [x] 존재하지 않는 이메일이 사용되면 400 Bad Request 상태코드를 반환한다
- [x] 잘못된 비밀번호가 사용되면 400 Bad Request 상태코드를 반환한다

#### 판매자 정보 조회

요청
- 메서드: GET
- 경로: /seller/me
- 헤더
  ```
  Authorization: Bearer {accessToken}
  ```

응답
- 200 OK
- 본문
  ```
  SellerMeView {
    id: string,
    email: string,
    username: string
  }
  ```

테스트
- [x] 올바르게 요청하면 200 OK 상태코드를 반환한다
- [x] 올바르게 요청하면 판매자 정보를 반환한다
- [x] 접근토큰을 사용하지 않으면 401 Unauthorized 상태코드를 반환한다

#### 구매자 정보 조회

요청
- 메서드: GET
- 경로: /shopper/me
- 헤더
  ```
  Authorization: Bearer {accessToken}
  ```

응답
- 200 OK
- 본문
  ```
  ShopperMeView {
    id: string,
    email: string,
    username: string
  }
  ```

테스트
- [x] 올바르게 요청하면 200 OK 상태코드를 반환한다
- [x] 올바르게 요청하면 구매자 정보를 반환한다

#### 판매자 상품 등록

요청
- 메서드: POST
- 경로: /seller/products
- 헤더
  ```
  Authorization: Bearer {token}
  ```
- 본문
  ```
  RegisterProductCommand {
    name: string,
    description: string,
    priceAmount: number,
    stockQuantity: number
  }
  ```

응답
- 201 Created

테스트
- [ ] 올바르게 요청하면 201 Created 상태코드를 반환한다
- [ ] 올바르게 요청하면 등록된 상품 정보에 접근하는 Location 헤더를 반환한다
- [ ] 잘못된 접근토큰을 사용하면 401 Unauthorized 상태코드를 반환한다
