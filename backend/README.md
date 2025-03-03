#  THE_GYM

##  개발 배경 
  주말 및 공휴일, 그리고 헬스장 사정으로 메인으로 삼은 헬스장이 휴관하는 경우 주변에 현재 운영 중인 헬스장이 있는지 확인하고 방문 할 수 있었으면 해서 개발 착수

##  1차 개발 단계에서 구현 할 기능 
  1.  내 현재 위치를 기준으로 반경 1km 내 운영 중인 헬스장 모두 표시
  2.  지도에 표시된 헬스장 마크를 클릭하면 헬스장 정보 확인 가능 ( 예 : 운영중인지? 일권은 얼마인지? 등등 )
  3.  실제 방문 해 보고 해당 헬스장이 마음에 들었으면 즐겨찾기 추가.

###  1차 개발 완료 후 고려해 볼 추가 기능 
  1.  체크인 기능
  2.  트레이닝 내용 기록 

##  개발에 이용한 기술 스택
  ##  backend 기술 스택 
    1.  Kotlin : 주요 개발 언어
    2.  Spring boot : backend frame work
    3.  Spring Security : 인증 및 보안 관리
    4.  JPA : ORM 사용 
    5.  REST API : 클라이언트와 서버간의 통신 방식 
    6.  JWT : 인증 및 토큰 기반 보안 

  ##  Frontend 기술 스택
    1.  React : 주요 frontend 라이브러리
    2.  React Hooks : 상태 및 생명주기 관리 
    3.  Axios : HTTP 요청 처리 
    4.  React Router : 페이지 이동 및 라우팅 

  ##  DB(RDBS)
    1.  MySQL : 데이터 저장소
    2.  Spring Data JPA : DB 연동 

  ##  외부 API 
    1.  네이버 지도 API : 위치 기반 서비스 활용
      1-1.  Web Dynamic Map : 웹 브라우저에 지도 표시 
      1-2.  Mobile Dynamic Map : 모바일 브라우저에 지도 표시 
      1-3.  Reverse Geocoding : 위도 및 경도를 주소로 변환
    2.  네이버 지역 검색 API : 
      2-1.  헬스장 정보를 검색하여 지도에 표시
      2-2.  지도 마커 클릭 시 해당 헬스장의 상세 정보를 제공 (운영 여부, 요금 등)
    3.  네이버 로그인 API
      3-1.  로그인 시 네이버에서 받아올 유저 정보는 유저 이름과 이메일 주소 두개만 채용 예정

  ##  추후에 추가할 기술스택 
    1.  배포 예정 환경: AWS (EC2, RDS)
      1-1.  도커
        1-1-1.  nginx를 이용하여 port forwarding 기능 제공 
        1-1-2.  Docker Compose 기능을 통해 배포 자동화 기능 도입 
      1-2.  Github Action 
        1-2-1.  Github Action을 통해 무중단 배포 기능 구현 

#### asd