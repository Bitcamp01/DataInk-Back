package com.bit.datainkback.controller.mongo;


import com.bit.datainkback.entity.mongo.TaskDetails;
import com.bit.datainkback.service.mongo.TaskDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mongo/task-details")
public class TaskDetailsController {
    @Autowired
    private TaskDetailsService taskDetailsService;

    @PostMapping
    public TaskDetails createTask(@RequestBody TaskDetails taskDetails) {
        return taskDetailsService.createTaskDetails(taskDetails);
    }

    @GetMapping("/{id}")
    public TaskDetails getTaskById(@PathVariable String id) {
        return taskDetailsService.getTaskDetailsById(id);
    }

    @GetMapping
    public List<TaskDetails> getAllTasks() {
        return taskDetailsService.getAllTaskDetails();
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        taskDetailsService.deleteTaskDetailsById(id);
    }
}
