# 2025-03-10

1. application.properties 환경변수가 있는파일을 무조건 숨겨야하느냐?
   - 공개해도 되는 설정값은 application.properties 파일에 올림
   - 보안상 숨겨야하는 값은 application-db, application-secret 등 설정파일을 따로 만들어서 gitignore 에 추가
   - 환경 변수를 파일로 나누는 방법을 찾아보고 적용
   - JWT Token KEY 를 환경변수 파일로 빼고 환경변수에서 값을 받아서 적용
2. config, mapper, model, util, filter 등 과 같이 공통적으로 사용하는 패키지는 common 패키지를 만들어서 하위로 이동
3. Controller, Service 역할에 대해서 스터디 후 블로그 포스팅
   - Controller 에는 요청 및 응답 + 값의 검증
   - Service 는 비즈니스 로직
4. Valid, Validation 차이에 대해서 스터디 후 블로그 포스팅
   - Valid 어노테이션에서 쓸 수 있는 어노테이션 종류 스터디 블로그 포스팅
5. 네이밍만 보고도 어떤 역할을 하는지 알 수 있도록 전체적인 네이밍 수정
6. gradle_wrapper.jar gitignore 에서 제외하기