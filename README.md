# 웹 애플리케이션 서버
## 미션 설명
간단한 HTTP 웹 서버를 만들어본다.
저장소에서 소스코드를 받아와서 메인 클래스를 실행하면 HTTP 서버가 실행된다. 웹브라우저로 로컬 서버 http://localhost:8080에 접속하면 Hello world가 보인다.


## 기능 요구사항
1. GET /index.html 응답하기
   http://localhost:8080/index.html에 접근할 수 있도록 구현한다.
   RequestHandlerTest 테스트가 모두 통과하도록 구현한다.

* HTTP Request Header 예
```
  GET /index.html HTTP/1.1
  Host: localhost:8080
  Connection: keep-alive
  Accept: */*
```


2. CSS 지원하기
   인덱스 페이지에 접속하면, 현재 stylesheet 파일을 지원하지 못하고 있다. Stylesheet 파일을 지원하도록 구현하도록 한다.

* HTTP Request Header 예
```
GET ./css/style.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
```


3. Query String 파싱
   “회원가입” 메뉴를 클릭하면 http://localhost:8080/user/form.html 으로 이동하면서 회원가입할 수 있다.
   회원가입을 하면 다음과 같은 형태로 사용자가 입력한 값이 서버에 전달된다.
   HTML과 URL을 비교해 보고 사용자가 입력한 값을 파싱해 model.User 클래스에 저장한다.
   회원가입할 때 생성한 User 객체를 DataBase.addUser() 메서드를 활용해 RAM 메모리에 저장한다.

* HTTP Request Header 예

```
  GET /user/create?userId=cu&password=password&name=%EC%9D%B4%EB%8F%99%EA%B7%9C&email=brainbackdoor%40gmail.com HTTP/1.1
  Host: localhost:8080
  Connection: keep-alive
  Accept: */*
```


4. POST 방식으로 회원가입
   http://localhost:8080/user/form.html 파일의 form 태그 method를 get에서 post로 수정한 후 회원가입 기능이 정상적으로 동작하도록 구현한다.
* HTTP Request Header 예
```  POST /user/create HTTP/1.1
  Host: localhost:8080
  Connection: keep-alive
  Content-Length: 59
  Content-Type: application/x-www-form-urlencoded
  Accept: */*

userId=cu&password=password&name=%EC%9D%B4%EB%8F%99%EA%B7%9C&email=brainbackdoor%40gmail.com
```

5. Redirect
   현재는 “회원가입”을 완료 후, URL이 /user/create 로 유지되는 상태로 읽어서 전달할 파일이 없다. redirect 방식처럼 회원가입을 완료한 후 index.html로 이동해야 한다.