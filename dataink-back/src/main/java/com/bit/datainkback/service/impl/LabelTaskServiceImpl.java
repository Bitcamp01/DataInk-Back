package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.entity.LabelTask;
import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.enums.TaskLevel;
import com.bit.datainkback.enums.TaskStatus;
import com.bit.datainkback.repository.LabelTaskRepository;
import com.bit.datainkback.repository.mongo.FieldRepository;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import com.bit.datainkback.service.LabelTaskService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;

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

    @Override
    public void rejectLabelTask(String taskId, String rejectionReason , Map<String, Object> transformedData) {
        // MongoDB에서 Tasks 문서를 조회
        Tasks tasks = mongoLabelTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tasks not found"));

        // MySQL에서 refTaskId에 해당하는 LabelTask 조회
        String refTaskId = tasks.getId(); // tasks에서 refTaskId를 가져옴
        LabelTask labelTask = labelTaskRepository.findByRefTaskId(refTaskId)
                .orElseThrow(() -> new RuntimeException("LabelTask not found"));

        // LabelTask 업데이트
        labelTask.setRejectionReason(rejectionReason);
        labelTask.setStatus(TaskStatus.IN_PROGRESS);
        labelTask.setReviewed(new Timestamp(System.currentTimeMillis()));

        labelTaskRepository.save(labelTask);

        // Tasks 상태 업데이트
        tasks.setStatus("in_progress");
        tasks.setFieldValue(transformedData); // transformedData를 fieldValue에 저장
        mongoTemplate.save(tasks);
    }

    @Override
    public void approveLabelTask(String taskId, String comment, Map<String, Object> transformedData) {
        // MongoDB에서 Tasks 문서를 조회
        Tasks tasks = mongoLabelTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tasks not found"));

        // MySQL에서 refTaskId에 해당하는 LabelTask 조회
        String refTaskId = tasks.getId(); // tasks에서 refTaskId를 가져옴
        LabelTask labelTask = labelTaskRepository.findByRefTaskId(refTaskId)
                .orElseThrow(() -> new RuntimeException("LabelTask not found"));

        // LabelTask 업데이트
        labelTask.setComment(comment);
        labelTask.setStatus(TaskStatus.REVIEWED);
        labelTask.setReviewed(new Timestamp(System.currentTimeMillis()));
        labelTaskRepository.save(labelTask);

        // Tasks 상태 업데이트
        tasks.setStatus("reviewed");
        tasks.setFieldValue(transformedData); // transformedData를 fieldValue에 저장
        mongoTemplate.save(tasks);
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
        Tasks tasks = mongoLabelTaskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Tasks not found"));
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

