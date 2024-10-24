package com.bit.datainkback.service.mongo;

import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoLabelTaskService {

    @Autowired
    private MongoLabelTaskRepository mongoLabelTaskRepository;

    // 데이터 저장
    public Tasks saveTask(Tasks task) {
        return mongoLabelTaskRepository.save(task);
    }

    // 모든 작업 가져오기
    public List<Tasks> getAllTasks() {
        return mongoLabelTaskRepository.findAll();
    }

    // 작업 ID로 특정 작업 가져오기
    public Tasks getTaskById(String id) {
        return mongoLabelTaskRepository.findById(id).orElse(null);
    }

    // 작업 ID로 특정 작업 삭제하기
    public void deleteTask(String id) {
        mongoLabelTaskRepository.deleteById(id);
    }

    // 기타 추가 로직 구현 가능
}
