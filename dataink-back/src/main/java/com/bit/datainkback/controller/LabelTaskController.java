package com.bit.datainkback.controller;

import com.bit.datainkback.dto.RejectReasonDto;
import com.bit.datainkback.service.LabelTaskService;
import com.bit.datainkback.service.SourceDataService;
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
public class LabelTaskController<LabelTaskDTO, SourceDataDTO> {

    private final LabelTaskService labelTaskService;
//    private final SourceDataService sourceDataService;
//
//    // 작업 목록 조회(staus가 )
//    @GetMapping("/tasks")
//    public ResponseEntity<List<LabelTaskDTO>> getAllTasks() {
//        List<LabelTaskDTO> tasks = labelTaskService.getAllTasks();
//        return ResponseEntity.ok(tasks);
//    }
//
//    // 특정 작업 조회 (taskId로 작업을 가져옴, 해당 작업의 status는 "pending"으로)
//    @GetMapping("/tasks/{taskId}")
//    public ResponseEntity<LabelTaskDTO> getTaskById(@PathVariable Long taskId) {
//        LabelTaskDTO task = labelTaskService.getTaskById(taskId);
//        return ResponseEntity.ok(task);
//    }
//
//    // 특정 작업의 원천 데이터(PDF) 가져오기
//    @GetMapping("/source/{sourceId}")
//    public ResponseEntity<SourceDataDTO> getSourceData(@PathVariable Long sourceId) {
//        SourceDataDTO sourceData = sourceDataService.getSourceDataBySourceId(sourceId); // source_id로 원천 데이터 가져오기
//        return ResponseEntity.ok(sourceData);
//    }
//
    // 반려 사유 전달하고 작업 상태(status)를 in_progress로 바꾼다.
    @PatchMapping("/reject")
    public ResponseEntity<Void> rejectTask(
            @RequestParam Long taskId,
            @RequestParam String rejectionReason
    ) {
        Timestamp reviewedTimestamp = new Timestamp(System.currentTimeMillis());
        labelTaskService.rejectLabelTask(taskId, rejectionReason, reviewedTimestamp);
        return ResponseEntity.ok().build();
    }

    // 검수 승인(검수 승인 코멘트를 보내고 작업 상태(status)를 reviewed로 바꾼다)
    @PatchMapping("/approve")
    public ResponseEntity<Void> approveTask(
            @RequestParam Long taskId,
            @RequestParam String comment
    ) {
        Timestamp reviewedTimestamp = new Timestamp(System.currentTimeMillis());
        labelTaskService.approveLabelTask(taskId, comment, reviewedTimestamp);
        return ResponseEntity.ok().build();
    }


    //putmapping으로 할지 patchmapping으로 할지 고민중
}
