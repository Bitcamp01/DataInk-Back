package com.bit.datainkback.controller.mongo;

import com.bit.datainkback.entity.mongo.BeforeLabelTask;
import com.bit.datainkback.service.mongo.BeforeLabelTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mongo/before-tasks")
public class BeforeLabelTaskController {
    @Autowired
    private BeforeLabelTaskService beforeLabelTaskService;

    @PostMapping("/create")
    public ResponseEntity<BeforeLabelTask> createLabelTask(
            @RequestBody BeforeLabelTask labelTask,
            @RequestParam Long projectId) {
        BeforeLabelTask savedTask = beforeLabelTaskService.createBeforeLabelTask(labelTask, projectId);
        return ResponseEntity.ok(savedTask);  // 저장된 MongoDB 데이터를 반환
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeforeLabelTask> getLabelTask(@PathVariable String id) {
        return beforeLabelTaskService.getBeforeLabelTaskById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
}