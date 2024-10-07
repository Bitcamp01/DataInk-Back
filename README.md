# 백엔드 작업 단계 지침(수정 중)

## 1. 기본 규칙
- 자신이 맡은 영역 이외의 폴더는 건드리지 않기. 필요한 경우엔 담당하는 사람과 얘기할 것
- DB 설정 변경 관련은 조장에게 얘기하고 변경하기

## 2. 파일 및 폴더 구조

  ### 파일 구조 초안:

```
src/main/java
└── com
    └── example
        └── project
            ├── config                // 보안, 데이터베이스, 메시징 시스템 설정 관련
            │   ├── RedisConfig.java          // Redis 설정
            │   ├── MongoConfig.java          // MongoDB 설정
            │   └── KafkaConfig.java          // Kafka 설정
            ├── controller            // REST API 컨트롤러
            │   ├── UserController.java           // 사용자 관련 API
            │   ├── ProjectController.java        // 프로젝트 관련 API
            │   ├── LabelTaskController.java      // 라벨링 작업 관련 API
            │   ├── LabelReviewController.java    // 검수 작업 관련 API
            │   ├── NoticeController.java         // 공지사항 관련 API
            │   ├── CommentController.java        // 댓글 관련 API
            │   ├── TempTaskController.java       // 임시 저장 관련 API
            │   ├── SourceDataController.java     // 원천 데이터 관련 API
            │   ├── NotificationController.java   // 실시간 알림 API (Redis 기반)
            │   └── ChatController.java           // 실시간 채팅 API (Redis 기반)
            ├── dto                   // 데이터 전송 객체 (DTO)
            │   ├── UserDTO.java
            │   ├── ProjectDTO.java
            │   ├── LabelTaskDTO.java
            │   ├── LabelReviewDTO.java
            │   ├── TempTaskDTO.java
            │   ├── NotificationDTO.java
            │   ├── ChatMessageDTO.java
            │   └── KafkaMessageDTO.java          // Kafka 메시지 전송 객체
            ├── entity                // JPA 엔티티 (DB 테이블 매핑)
            │   ├── User.java
            │   ├── Project.java
            │   ├── UserProject.java
            │   ├── LabelField.java
            │   ├── LabelTask.java
            │   ├── LabelReview.java
            │   ├── Notice.java
            │   ├── Comment.java
            │   ├── TempTask.java
            │   ├── SourceData.java
            │   ├── UserDetail.java
            │   └── Reject.java
            ├── exception             // 예외 처리 관련
            ├── repository            // JPA 및 MongoDB 레포지토리 (데이터 접근 계층)
            │   ├── UserRepository.java
            │   ├── ProjectRepository.java
            │   ├── LabelTaskRepository.java
            │   ├── LabelReviewRepository.java
            │   ├── NoticeRepository.java
            │   ├── CommentRepository.java
            │   ├── TempTaskRepository.java
            │   ├── SourceDataRepository.java
            │   ├── RedisChatRepository.java      // Redis 기반 채팅 데이터 처리
            │   └── MongoLabelDataRepository.java // MongoDB 기반 라벨링 데이터 처리
            ├── security              // Spring Security 설정
            │   └── SecurityConfig.java
            ├── service               // 서비스 계층 (비즈니스 로직)
            │   │   └── impl (impl 폴더 파일은 각자 구현)
            │   ├── UserService.java
            │   ├── ProjectService.java
            │   ├── LabelTaskService.java
            │   ├── LabelReviewService.java
            │   ├── NoticeService.java
            │   ├── CommentService.java
            │   ├── TempTaskService.java
            │   ├── SourceDataService.java
            │   ├── NotificationService.java      // Redis 기반 실시간 알림 서비스
            │   ├── ChatService.java              // Redis 기반 실시간 채팅 서비스
            │   └── KafkaService.java             // Kafka 메시지 큐 서비스
            ├── util                  // 유틸리티 클래스 (공용 기능)
            │   ├── RedisUtil.java             // Redis 유틸리티
            │   ├── KafkaUtil.java             // Kafka 유틸리티
            │   └── MongoUtil.java             // MongoDB 유틸리티

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

