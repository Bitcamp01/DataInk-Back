package com.bit.datainkback.controller.mongo;

import com.bit.datainkback.entity.mongo.MongoLabelTask;
import com.bit.datainkback.service.mongo.MongoLabelTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo/tasks")
public class MongoLabelTaskController {

    @Autowired
    private MongoLabelTaskService mongoLabelTaskService;

    @GetMapping
    public List<MongoLabelTask> getAllTasks() {
        return mongoLabelTaskService.getAllTasks();
    }

    @PostMapping
    public MongoLabelTask createTask(@RequestBody MongoLabelTask task) {
        return mongoLabelTaskService.saveTask(task);
    }

    @GetMapping("/{id}")
    public MongoLabelTask getTaskById(@PathVariable String id) {
        return mongoLabelTaskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        mongoLabelTaskService.deleteTask(id);
    }
}
