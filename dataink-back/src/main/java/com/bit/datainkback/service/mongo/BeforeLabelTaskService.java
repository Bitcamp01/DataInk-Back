package com.bit.datainkback.service.mongo;

import com.bit.datainkback.entity.mongo.BeforeLabelTask;
import com.bit.datainkback.repository.mongo.BeforeLabelTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BeforeLabelTaskService {
    @Autowired
    private BeforeLabelTaskRepository beforeLabelTaskRepository;

    @Autowired
    private MySqlService mySqlService;  // MySQL에서 deadline을 가져오는 서비스

    public BeforeLabelTask createBeforeLabelTask(BeforeLabelTask beforeLabelTask, Long projectId) {
        // MySQL에서 deadline 가져오기
        LocalDateTime deadline = mySqlService.getEndDateByProjectId(projectId);

        // MongoDB에 저장할 데이터 설정
        beforeLabelTask.setEndDate(deadline);

        // MongoDB에 저장
        return beforeLabelTaskRepository.save(beforeLabelTask);  // 저장 시 id는 MongoDB에서 자동으로 생성
    }

    public Optional<BeforeLabelTask> getBeforeLabelTaskById(String id) {
        return beforeLabelTaskRepository.findById(id);  // MongoDB에서 id로 데이터 조회
    }
}
