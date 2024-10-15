package com.bit.datainkback.service.mongo;

import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import com.bit.datainkback.enums.TaskStatus;
import com.bit.datainkback.repository.mongo.MongoProjectDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoProjectDataService {

    @Autowired
    private MongoProjectDataRepository mongoProjectDataRepository;

    // MongoDB에 프로젝트 데이터 생성
    public String createMongoProjectData(Long projectId, List<Folder> folders) {
        // MongoDB에 프로젝트 데이터 저장
        MongoProjectData mongoProjectData = new MongoProjectData();
        mongoProjectData.setProjectId(projectId);
        mongoProjectData.setFolders(folders);  // 폴더 구조 포함

        MongoProjectData savedData = mongoProjectDataRepository.save(mongoProjectData); // MongoDB에 저장

        return savedData.getId();  // 저장된 MongoDB ID 반환
    }

    // 특정 프로젝트의 폴더와 라벨링 데이터 가져오기
    public MongoProjectData getProjectDataByProjectId(Long projectId) {
        return mongoProjectDataRepository.findByProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("MongoDB에서 프로젝트 데이터를 찾을 수 없습니다."));
    }

    public void updateTaskStatus(String taskId, TaskStatus newStatus) {
        // 작업 상태 업데이트 로직 (BeforeLabelTask -> MongoLabelTasks 전환)
    }
}

