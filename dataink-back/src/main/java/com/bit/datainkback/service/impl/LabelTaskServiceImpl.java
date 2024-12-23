package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.entity.LabelTask;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.enums.TaskLevel;
import com.bit.datainkback.enums.TaskStatus;
import com.bit.datainkback.repository.LabelTaskRepository;
import com.bit.datainkback.repository.NotificationRepository;
import com.bit.datainkback.repository.mongo.FieldRepository;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import com.bit.datainkback.service.LabelTaskService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;
import com.bit.datainkback.enums.NotificationType;
import java.time.LocalDateTime;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor // 생성자 자동 생성
public class LabelTaskServiceImpl implements LabelTaskService {

    private final LabelTaskRepository labelTaskRepository;
    private final MongoLabelTaskRepository mongoLabelTaskRepository;
    private final FieldRepository fieldRepository;
    private final MongoTemplate mongoTemplate;
    private final NotificationRepository notificationRepository;

    @Override
    public void rejectLabelTask(String taskId, String rejectionReason , Map<String, Object> transformedData, User joinedUser) {
        // MongoDB에서 Tasks 문서를 조회
        Tasks tasks = mongoLabelTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tasks not found"));

        // MySQL에서 refTaskId에 해당하는 LabelTask 조회
        String refTaskId = tasks.getId(); // tasks에서 refTaskId를 가져옴
        LabelTask labelTask = labelTaskRepository.findByRefTaskId(refTaskId)
                .orElseThrow(() -> new RuntimeException("LabelTask not found"));

        // LabelTask 업데이트
        labelTask.setRejectionReason(rejectionReason);
//        labelTask.setStatus(TaskStatus.IN_PROGRESS);
//        labelTask.setReviewed(new Timestamp(System.currentTimeMillis()));

        labelTaskRepository.save(labelTask);

        // Tasks 상태 업데이트
        tasks.setStatus("rejected");
        tasks.setFieldValue(transformedData); // transformedData를 fieldValue에 저장
        mongoTemplate.save(tasks);

        // 알림 테이블에 새로운 알림 생성
        Notification notification = Notification.builder()
                .user(joinedUser)
                .content(joinedUser.getName() + "님이" + tasks.getTaskName() + "작업을 반려 하였습니다.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .notificationType(NotificationType.TASK_UPDATED) // 알림 유형 설정
                .relatedId(labelTask.getTaskId()) // 프로젝트 Id(수정 필요함)
                .build();
        notificationRepository.save(notification); // Notification을 DB에 저장

        // Redis에 알림 저장 (userId를 키로 사용)

    }

    @Override
    public void approveLabelTask(String taskId, String comment, Map<String, Object> transformedData, User joinedUser) {
        // MongoDB에서 Tasks 문서를 조회
        Tasks tasks = mongoLabelTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tasks not found"));

        // MySQL에서 refTaskId에 해당하는 LabelTask 조회
        String refTaskId = tasks.getId(); // tasks에서 refTaskId를 가져옴
        LabelTask labelTask = labelTaskRepository.findByRefTaskId(refTaskId)
                .orElseThrow(() -> new RuntimeException("LabelTask not found"));

        // LabelTask 업데이트
        labelTask.setComment(comment);
        labelTaskRepository.save(labelTask);

        // Tasks 상태 업데이트
        tasks.setStatus("reviewed");
        tasks.setFieldValue(transformedData); // transformedData를 fieldValue에 저장
        mongoTemplate.save(tasks);

        // 알림 테이블에 새로운 알림 생성
        Notification notification = Notification.builder()
                .user(joinedUser)
                .content(joinedUser.getName() + "님이" + tasks.getTaskName() + "작업을 승인 하였습니다.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .notificationType(NotificationType.TASK_UPDATED) // 알림 유형 설정
                .relatedId(labelTask.getTaskId()) // 프로젝트 Id(수정 필요함)
                .build();
        notificationRepository.save(notification); // Notification을 DB에 저장

        // Redis에 알림 저장 (userId를 키로 사용)

    }

    @Override
    public List<LabelTaskDto> getAllLabelTasks() {
        // LabelTask 엔티티를 LabelTaskDto로 변환하여 반환
        return labelTaskRepository.findAll().stream()
                .map(LabelTask::toDto) // 엔티티의 toDto 메서드 사용
                .collect(Collectors.toList());
    }

    @Override
    public LabelTaskDto getLabelTaskById(Long taskId) {
        Optional<LabelTask> labelTask = labelTaskRepository.findById(taskId); // 수정: findByLabelId -> findById
        return labelTask.map(LabelTask::toDto).orElse(null);
    }


    // 1028 필드밸류 가져오기 위한 메서드 새로 만들어 봄
    public List<Field> getLabelTaskDetails(String taskId) {
        // Step 1: MongoDB에서 Tasks 조회
        Tasks tasks = mongoLabelTaskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Tasks not found")) ;
        // Step 2: 조회한 Tasks에서 itemIds 꺼내오기
        List<String> itemIds = tasks.getItemIds();

        List<Field> fieldList = new ArrayList<>();
        itemIds.forEach(itemId -> {
            System.out.println(itemId);
            Field field = fieldRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Field not found"));
            fieldList.add(field);
        });
        return fieldList;
    }

    public List<Object> getLabelDetails(String taskId) {
        // Step 1: MongoDB에서 Tasks 조회
        Tasks tasks = mongoLabelTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tasks not found"));

        // Step 2: 조회한 Tasks에서 fieldValue 가져오기
        Map<String, Object> fieldValuesMap = tasks.getFieldValue(); // Map<String, Object> 형태

        // Step 3: Map의 값들을 List로 변환
        List<Object> fieldValuesList = new ArrayList<>(fieldValuesMap.values());

        // fieldValues 확인 출력 (디버깅 목적)
        fieldValuesList.forEach(System.out::println);

        return fieldValuesList;
    }

    @Override
    public void saveLabelDetail(String taskId, Map<String, Object> transformedData, User joinedUser) {
        // MongoDB에서 Tasks 문서를 조회
        Tasks tasks = mongoLabelTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tasks not found"));

        // Tasks의 fieldValue 업데이트
        tasks.setFieldValue(transformedData); // transformedData를 fieldValue에 저장
        tasks.setStatus("submitted");
        mongoTemplate.save(tasks); // MongoDB에 저장

        LabelTask labelTask = LabelTask.builder()
                        .refTaskId(taskId)
                        .labeler(joinedUser)  // joinedUser로 설정
                        .build();

        labelTaskRepository.save(labelTask);

        // 알림 테이블에 새로운 알림 생성
        Notification notification = Notification.builder()
                .user(joinedUser)
                .content(joinedUser.getName() + "(라벨러)님이 " + tasks.getTaskName() + " 작업을 업로드 하였습니다.")
                //.content(joinedUser.getName() + "님이" + tasks.getTaskName() + "작업을 승인 하였습니다.")
                //.content(joinedUser.getName() + "님이" + tasks.getTaskName() + "작업을 반려 하였습니다.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .notificationType(NotificationType.TASK_UPDATED) // 알림 유형 설정
                .relatedId(labelTask.getTaskId()) // 프로젝트 Id(수정 필요함)
                .build();
        notificationRepository.save(notification); // Notification을 DB에 저장

        // Redis에 알림 저장 (userId를 키로 사용)
    }

    @Override
    public void adminApprove(String taskId, Map<String, Object> transformedData, User joinedUser) {
        // MongoDB에서 Tasks 문서를 조회
        Tasks tasks = mongoLabelTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tasks not found"));

        // Tasks의 fieldValue 업데이트
        tasks.setFieldValue(transformedData); // transformedData를 fieldValue에 저장
        tasks.setStatus("approved");
        mongoTemplate.save(tasks); // MongoDB에 저장

        // 알림 테이블에 새로운 알림 생성
        Notification notification = Notification.builder()
                .user(joinedUser)
                .content(joinedUser.getName() + "님이" + tasks.getTaskName() + "작업을 최종승인 하였습니다.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .notificationType(NotificationType.TASK_UPDATED) // 알림 유형 설정
//                .relatedId(labelTask.getTaskId()) // 프로젝트 Id(수정 필요함) , 이 부분 지금 이 메소드는 labeltask 사용 안해서 지금 선언 안해놨음
                .build();
        notificationRepository.save(notification); // Notification을 DB에 저장

        // Redis에 알림 저장 (userId를 키로 사용)


    }

    @Override
    public void createNewTask(String id) {
        LabelTask labelTask = new LabelTask();
        labelTask.setRefTaskId(id);
        labelTaskRepository.save(labelTask);
    }
}








// 유배지



//    // **LabelTask와 MongoDB의 Tasks 연결 정보를 조회하는 메서드**
//    public LabelTaskDto getLabelTaskWithTasks(Long taskId) {
//        LabelTask labelTask = labelTaskRepository.findById(taskId)
//                .orElseThrow(() -> new RuntimeException("LabelTask not found"));
//
//        // MongoDB에서 fieldId와 동일한 ID를 가진 Tasks 조회
//        Query query = new Query(Criteria.where("_id").is(labelTask.getFieldId()));
//        Tasks tasks = mongoTemplate.findOne(query, Tasks.class);
//
//        LabelTaskDto labelTaskDto = labelTask.toDto();
//        if (tasks != null) {
//            labelTaskDto.setTaskId(Long.parseLong(tasks.getId())); // Tasks의 ID 설정
//            // tasks.getStatus()는 Enum 타입이므로, 적절히 변환하여 설정
//            labelTaskDto.setStatus(TaskStatus.valueOf(tasks.getStatus())); // Status를 Enum으로 변환하여 설정
//        }
//
//        return labelTaskDto;
//    }

