package com.bit.datainkback.controller.mongo;

import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import com.bit.datainkback.service.mongo.MongoLabelTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mongo/tasks")
public class MongoLabelTaskController {

    @Autowired
    private MongoLabelTaskService mongoLabelTaskService;

    @Autowired
    private MongoLabelTaskRepository mongoLabelTaskRepository;

    @GetMapping
    public List<Tasks> getAllTasks() {
        return mongoLabelTaskService.getAllTasks();
    }

    @PostMapping
    public Tasks createTask(@RequestBody Tasks task) {
        return mongoLabelTaskService.saveTask(task);
    }

    @GetMapping("/{id}")
    public Tasks getTaskById(@PathVariable String id) {
        return mongoLabelTaskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        mongoLabelTaskService.deleteTask(id);
    }

    // 특정 폴더의 Task 목록 가져오기
    @GetMapping("/folder/{parentFolderId}")
    public ResponseEntity<List<Tasks>> getTasksByFolderId(@PathVariable String parentFolderId) {
        List<Tasks> tasks = mongoLabelTaskRepository.findByParentFolderId(parentFolderId);
        return ResponseEntity.ok(tasks);
    }

    // 여러 taskId로 상태를 업데이트하는 API
    @PutMapping("/update-submit")
    public ResponseEntity<?> updateTaskStatus(@RequestBody List<String> taskIds) {
        // taskId로 해당 task들을 찾아 상태를 업데이트
        List<Tasks> tasksToUpdate = mongoLabelTaskRepository.findAllById(taskIds);

        tasksToUpdate.forEach(task -> task.setStatus("submitted")); // 상태를 submitted로 변경

        mongoLabelTaskRepository.saveAll(tasksToUpdate);  // 변경된 task들을 저장

        return ResponseEntity.ok().build();  // 성공 응답 반환
    }
}
