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
}
