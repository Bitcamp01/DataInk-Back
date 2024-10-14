package com.bit.datainkback.controller.mongo;

import com.bit.datainkback.entity.mongo.MongoLabelTasks;
import com.bit.datainkback.service.mongo.MongoLabelTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mongo/tasks")
public class MongoLabelTaskController {

    @Autowired
    private MongoLabelTaskService mongoLabelTaskService;

    @GetMapping
    public List<MongoLabelTasks> getAllTasks() {
        return mongoLabelTaskService.getAllTasks();
    }

    @PostMapping
    public MongoLabelTasks createTask(@RequestBody MongoLabelTasks task) {
        return mongoLabelTaskService.saveTask(task);
    }

    @GetMapping("/{id}")
    public MongoLabelTasks getTaskById(@PathVariable String id) {
        return mongoLabelTaskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        mongoLabelTaskService.deleteTask(id);
    }
}
