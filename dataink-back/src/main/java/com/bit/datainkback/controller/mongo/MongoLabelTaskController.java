package com.bit.datainkback.controller.mongo;

import com.bit.datainkback.entity.mongo.Tasks;
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
}
