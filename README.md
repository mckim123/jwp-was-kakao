# 웹 애플리케이션 서버
## 미션 설명
간단한 HTTP 웹 서버를 만들어본다.
저장소에서 소스코드를 받아와서 메인 클래스를 실행하면 HTTP 서버가 실행된다. 웹브라우저로 로컬 서버 http://localhost:8080에 접속하면 Hello world가 보인다.


## 1단계
- [x] GET /index.html 응답하기
   http://localhost:8080/index.html에 접근할 수 있도록 구현한다.
   RequestHandlerTest 테스트가 모두 통과하도록 구현한다.

- [x] CSS 지원하기
   인덱스 페이지에 접속하면, 현재 stylesheet 파일을 지원하지 못하고 있다. Stylesheet 파일을 지원하도록 구현하도록 한다.


- [x] Query String 파싱
   “회원가입” 메뉴를 클릭하면 http://localhost:8080/user/form.html 으로 이동하면서 회원가입할 수 있다.
   회원가입을 하면 다음과 같은 형태로 사용자가 입력한 값이 서버에 전달된다.
   HTML과 URL을 비교해 보고 사용자가 입력한 값을 파싱해 model.User 클래스에 저장한다.
   회원가입할 때 생성한 User 객체를 DataBase.addUser() 메서드를 활용해 RAM 메모리에 저장한다.

* HTTP Request Header 예



- [x] POST 방식으로 회원가입
   http://localhost:8080/user/form.html 파일의 form 태그 method를 get에서 post로 수정한 후 회원가입 기능이 정상적으로 동작하도록 구현한다.

- [x] Redirect
   현재는 “회원가입”을 완료 후, URL이 /user/create 로 유지되는 상태로 읽어서 전달할 파일이 없다. redirect 방식처럼 회원가입을 완료한 후 index.html로 이동해야 한다.

  
## 2단계
- [x] 로그인 기능 구현
  - [x] 회원가입한 사용자로만 로그인할 수 있어야 한다.
  - [x] 로그인이 성공하면 index.html로 이동한다.
  - [x] 로그인이 실패하면 /user/login_failed.html로 이동해야 한다.
  - [x] Cookie 클래스를 추가하고 HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현한다.
  - [x] Set-Cookie 설정시 모든 요청에 대해 Cookie 처리가 가능하도록 Path 설정 값을 /로 설정한다.
  - [x] 응답 header에 Set-Cookie값을 설정한 후 요청 header에 Cookie이 전달되는지 확인한다.

- [x] 템플릿 엔진 활용하기
  - [x] 접근하고 있는 사용자가 “로그인” 상태일 경우(Cookie 값이 logined=true) 경우 http://localhost:8080/user/list 로 접근했을 때 사용자 목록을 출력한다.
  - [x] 만약 로그인하지 않은 상태라면 로그인 페이지(login.html)로 이동한다. 
  - [x] 동적으로 html을 생성하기 위해 handlebars.java template engine을 활용한다.

- [x] Session 구현하기
  - [x] 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크할 수 있어야 한다.
  - [x] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장해보자.
  - [x] 로그인된 상태에서 /user/login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.
