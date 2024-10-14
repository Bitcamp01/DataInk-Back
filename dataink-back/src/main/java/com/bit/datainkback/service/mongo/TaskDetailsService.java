package com.bit.datainkback.service.mongo;

import com.bit.datainkback.entity.mongo.TaskDetails;
import com.bit.datainkback.repository.mongo.TaskDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskDetailsService {
    @Autowired
    private TaskDetailsRepository taskDetailsRepository;

    public TaskDetails createTaskDetails(TaskDetails taskDetails) {
        return taskDetailsRepository.save(taskDetails);
    }

    public TaskDetails getTaskDetailsById(String id) {
        return taskDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MongoDB에서 항목 데이터를 찾을 수 없습니다."));
    }

    public List<TaskDetails> getAllTaskDetails() {
        return taskDetailsRepository.findAll();
    }

    public void deleteTaskDetailsById(String id) {
        taskDetailsRepository.deleteById(id);
    }
}
