package com.bit.datainkback.controller;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.service.LabelTaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/labeltask")
public class LabelTaskController {

    private final LabelTaskService labelTaskService;

    // 반려 사유 전달하고 작업 상태(status)를 in_progress로 바꾼다.
    @PatchMapping("/reject")
    public ResponseEntity<Void> rejectTask(
            @RequestParam Long taskId,
            @RequestParam String rejectionReason
    ) {
        log.info("Rejecting task with ID: {} and reason: {}", taskId, rejectionReason);
        Timestamp reviewedTimestamp = new Timestamp(System.currentTimeMillis());
        labelTaskService.rejectLabelTask(taskId, rejectionReason, reviewedTimestamp);
        log.info("Task with ID: {} successfully rejected", taskId);
        return ResponseEntity.ok().build();
    }

    // 검수 승인(검수 승인 코멘트를 보내고 작업 상태(status)를 reviewed로 바꾼다)
    @PatchMapping("/approve")
    public ResponseEntity<Void> approveTask(
            @RequestParam Long taskId,
            @RequestParam String comment
    ) {
        log.info("Approving task with ID: {} and comment: {}", taskId, comment);
        Timestamp reviewedTimestamp = new Timestamp(System.currentTimeMillis());
        labelTaskService.approveLabelTask(taskId, comment, reviewedTimestamp);
        log.info("Task with ID: {} successfully approved", taskId);
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
}
