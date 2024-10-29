package com.bit.datainkback.controller;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.service.LabelTaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/labeltask")
public class LabelTaskController {

    private final LabelTaskService labelTaskService;

    // 반려 사유 전달하고 작업 상태(status)를 in_progress로 바꾼다.
    @PatchMapping("/reject")
    public ResponseEntity<Void> rejectTask(
            @RequestParam String taskId, // taskId로 변경
            @RequestBody Map<String, Object> requestBody
    ) {
        String rejectionReason = (String) requestBody.get("rejectionReason");
        Map<String, Object> transformedData = (Map<String, Object>) requestBody.get("transformedData");
        log.info("Rejecting task with Task ID: {} and reason: {}", taskId, rejectionReason);
        labelTaskService.rejectLabelTask(taskId, rejectionReason, transformedData); // 서비스 호출
        return ResponseEntity.ok().build();
    }

    // 검수 승인(검수 승인 코멘트를 보내고 작업 상태(status)를 reviewed로 바꾼다)
    @PatchMapping("/approve")
    public ResponseEntity<Void> approveTask(
            @RequestParam String taskId, // MongoDB의 Tasks ID를 사용
            @RequestBody Map<String, Object> requestBody
    ) {
        String comment = (String) requestBody.get("comment");
        Map<String, Object> transformedData = (Map<String, Object>) requestBody.get("transformedData");
        log.info("Rejecting task with Task ID: {} and reason: {}", taskId, comment);
        labelTaskService.rejectLabelTask(taskId, comment, transformedData); // 서비스 호출
        return ResponseEntity.ok().build();
    }

    // 데이터를 가져오는 엔드포인트 추가
    @GetMapping("/data")
    public ResponseEntity<List<LabelTaskDto>> getData() {
        log.info("Fetching all label tasks data");
        List<LabelTaskDto> dataList = labelTaskService.getAllLabelTasks();
        log.info("Fetched {} tasks", dataList.size());
        return ResponseEntity.ok(dataList);
    }

    // 특정 taskId로 데이터를 조회하는 엔드포인트
    @GetMapping("/data/{taskId}")
    public ResponseEntity<LabelTaskDto> getLabelTaskById(@PathVariable Long taskId) {
        LabelTaskDto labelTask = labelTaskService.getLabelTaskById(taskId);
        if (labelTask == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(labelTask);
    }


    // 1028 필드밸류 가져오기 위한 메서드 새로 작성해봄
    @GetMapping("/taskDetails/{taskId}")
    public ResponseEntity<List<Field>> getLabelTaskDetails(@PathVariable String taskId) {
        List<Field> labelTaskDto = labelTaskService.getLabelTaskDetails(taskId);
        return ResponseEntity.ok(labelTaskDto);
    }
}
