# 백엔드 작업 단계 지침(수정 중)

## 1. 기본 규칙
- 자신이 맡은 영역 이외의 폴더는 건드리지 않기. 필요한 경우엔 담당하는 사람과 얘기할 것
- DB 설정 변경 관련은 조장에게 얘기하고 변경하기
- intellij로 폴더 잡을 때 DataInk-Back이 아니라 dataink-back을 잡기

## 2. 파일 및 폴더 구조

  ### 파일 구조(24.10.16 갱신):

```
src/main/java
└── com
    └── bit
        └── datainkback
            ├── common                // 공통 유틸리티 클래스
            │   ├── FileUtils.java          // 파일 관련 유틸리티
            │   ├── KafkaUtil.java          // Kafka 메시징 관련 유틸리티
            │   ├── MongoUtil.java          // MongoDB 관련 유틸리티
            │   └── RedisUtil.java          // Redis 관련 유틸리티
            ├── config                // 보안, 데이터베이스, 메시징 시스템 설정
            │   ├── KafkaConfiguration.java          // Kafka 설정 클래스
            │   ├── MongoConfiguration.java          // MongoDB 설정 클래스
            │   ├── NaverConfiguration.java          // 네이버 클라우드 설정 클래스
            │   ├── QueryDslConfiguration.java       // QueryDSL 설정 클래스
            │   ├── RedisConfiguration.java          // Redis 설정 클래스
            │   ├── SecurityConfiguration.java       // 보안 및 인증 설정 (JWT 등)
            │   └── WebMvcConfiguration.java         // 웹 MVC 설정
            ├── controller            // REST API 컨트롤러 (사용자 요청 처리)
            │   │   └── mongo (MongoDB 관련 컨트롤러)
            │   ├── UserController.java           // 사용자 관련 API
            │   ├── ProjectController.java        // 프로젝트 관련 API
            │   ├── LabelTaskController.java      // 라벨링 작업 및 검수 API
            │   ├── LabelFieldsController.java    // 라벨링 필드 관련 API
            │   ├── MemberManagementController.java // 사용자 관리 API
            │   ├── MypageController.java         // 마이페이지 관련 API
            │   ├── NoticeController.java         // 공지사항 관련 API
            │   ├── CommentController.java        // 댓글 관련 API
            │   ├── TempTaskController.java       // 임시 작업 저장 API
            │   ├── SourceDataController.java     // 원천 데이터 처리 API
            │   ├── NotificationController.java   // 실시간 알림 API (Redis 기반)
            │   └── ChatController.java           // 실시간 채팅 API (Redis 기반)
            ├── dto                   // 데이터 전송 객체 (DTO)
            │   │   └── mongo (MongoDB 관련 DTO)
            │   ├── ChatMessageDto.java           // 채팅 메시지 DTO
            │   ├── CommentDto.java               // 댓글 DTO
            │   ├── KafkaMessageDto.java          // Kafka 메시지 DTO
            │   ├── LabelFieldDto.java            // 라벨 필드 DTO
            │   ├── LabelTaskDto.java             // 라벨 작업 DTO
            │   ├── NoticeDto.java                // 공지사항 DTO
            │   ├── NoticeFileDto.java            // 공지 파일 DTO
            │   ├── NotificationDto.java          // 알림 DTO
            │   ├── ProjectDto.java               // 프로젝트 DTO
            │   ├── RejectReasonDto.java          // 반려 사유 DTO
            │   ├── ResponseDto.java              // 공통 응답 DTO
            │   ├── SourceDataDto.java            // 원천 데이터 DTO
            │   ├── TempTaskDto.java              // 임시 작업 DTO
            │   ├── UserDetailDto.java            // 사용자 상세 정보 DTO
            │   ├── UserDto.java                  // 사용자 정보 DTO
            │   └── UserProjectDto.java           // 사용자-프로젝트 연관 DTO
            ├── entity                // JPA 엔티티 (DB 테이블 매핑)
            │   │   └── mongo (MongoDB 관련 엔티티)
            │   ├── Comment.java                  // 댓글 엔티티
            │   ├── CustomUserDetails.java        // 사용자 인증 정보 엔티티
            │   ├── LabelField.java               // 라벨 필드 엔티티
            │   ├── LabelTask.java                // 라벨 작업 엔티티
            │   ├── Notice.java                   // 공지사항 엔티티
            │   ├── NoticeFile.java               // 공지 파일 엔티티
            │   ├── Project.java                  // 프로젝트 엔티티
            │   ├── SourceData.java               // 원천 데이터 엔티티
            │   ├── TempTask.java                 // 임시 작업 엔티티
            │   ├── User.java                     // 사용자 엔티티
            │   ├── UserDetail.java               // 사용자 상세 정보 엔티티
            │   ├── UserProject.java              // 사용자-프로젝트 연관 엔티티
            │   └── UserProjectId.java            // 사용자-프로젝트 복합 키 엔티티
            ├── enums // enum 타입 관리
            ├── jwt // JWT 인증 관련 폴더
            ├── listener // 이벤트 리스너 관련 폴더
            ├── repository            // JPA 및 MongoDB 레포지토리 (데이터 접근 계층)
            │   │   └── custom (복잡한 쿼리를 위한 커스텀 레포지토리)
            │   │   └── mongo (MongoDB 관련 레포지토리)
            │   ├── CommentRepository.java        // 댓글 레포지토리
            │   ├── LabelFieldRepository.java     // 라벨 필드 레포지토리
            │   ├── LabelTaskRepository.java      // 라벨 작업 레포지토리
            │   ├── NoticeFileRepository.java     // 공지 파일 레포지토리
            │   ├── NoticeRepository.java         // 공지사항 레포지토리
            │   ├── ProjectRepository.java        // 프로젝트 레포지토리
            │   ├── RedisChatRepository.java      // Redis 채팅 레포지토리
            │   ├── SourceDataRepository.java     // 원천 데이터 레포지토리
            │   ├── TempTaskRepository.java       // 임시 작업 레포지토리
            │   ├── UserDetailRepository.java     // 사용자 상세 정보 레포지토리
            │   ├── UserProjectRepository.java    // 사용자-프로젝트 연관 레포지토리
            │   └── UserRepository.java           // 사용자 레포지토리
            ├── service               // 서비스 계층 (비즈니스 로직)
            │   │   └── impl (각 서비스의 구현 클래스)
            │   │   └── mongo (MongoDB 관련 서비스)
            │   ├── ChatService.java              // 채팅 서비스
            │   ├── CommentService.java           // 댓글 서비스
            │   ├── KafkaService.java             // Kafka 메시징 서비스
            │   ├── LabelReviewService.java       // 라벨 검수 서비스
            │   ├── LabelTaskService.java         // 라벨 작업 서비스
            │   ├── MypageService.java            // 마이페이지 서비스
            │   ├── NoticeService.java            // 공지사항 서비스
            │   ├── NotificationService.java      // 알림 서비스
            │   ├── ProjectService.java           // 프로젝트 서비스
            │   ├── SourceDataService.java        // 원천 데이터 서비스
            │   ├── TempTaskService.java          // 임시 작업 서비스
            │   └── UserService.java              // 사용자 서비스

src/main/resources
├── application.properties           // 애플리케이션 설정 파일 (gitignore에 올려두었으므로 슬랙 참고해서 각자 설정할 것)
└── templates                        // HTML 템플릿 (미사용)

```

## 3. 협업 규칙(Git)
  ### 브랜치 구조

1. **main 브랜치**
   - 배포 가능한 최종 코드가 저장되는 브랜치
   - 직접 개발하지 않으며, **모든 기능이 검증된 후에만 병합**

2. **develop 브랜치**
   - 개발 중인 코드가 모여있는 브랜치
   - 각 기능(feature) 브랜치에서 작업한 코드를 병합하는 중심 브랜치

3. **feature 브랜치**
   - 새로운 기능 개발 또는 버그 수정을 위한 독립적인 작업 브랜치
   - 기능 개발이 완료되면 **develop 브랜치**에 병합

```
main                  # 최종 배포 브랜치
│
└── develop           # 모든 기능이 병합되는 개발 브랜치
   │         
   ├── feat/login-page        # '로그인 페이지' 기능 개발 브랜치
   │   
   ├── feat/user-profile      # '사용자 프로필' 기능 개발 브랜치
   │   
   └── feat/payment-system    # '결제 시스템' 기능 개발 브랜치

```

  ### 커밋 컨벤션
  1. **영어컨벤션 + 한글 커밋 메시지** 형식으로 작성

  2. **영어컨벤션**은 5개만 사용 (Feat, Add, Modify, Style, Delete)
  - **Feat** : 기능 추가 (브랜치 맨처음 커밋할때, 기능 추가할때)
  - **Add** : 코드 추가 (어떠한 기능 내에 기능을 더 추가할 때)
  - **Modify** : 코드 수정 ( 버그 수정, 코드 지우고, 추가하고, 수정하는 모든 과정들 )
  - **Style** : 컴포넌트 및 UI 구현 (스타일드 컴포넌트)
  - **Delete** : 코드 삭제 (코드만 지우는것)

  3. 제목은 **50글자 이내**

  4. **제목 + 본문(선택)** 으로 구성 / 제목에서 설명하지 못하는 경우 본문에 자세히 작성

  5. 커밋메세지는 무엇을 했는지 파악할 수 있게 **자세히 작성**

  6. 어떻게 보다는 **무엇과 왜**를 설명하기

  7. 제목은 **명령문O, 과거형 X**

  8. 제목의 끝에는 **마침표를 넣지 않기**

  9. 한 개의 커밋에는 한 개의 기능/변경사항만 작성하도록 노력. **1커밋 1기능**

