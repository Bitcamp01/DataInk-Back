package com.bit.datainkback.service.mongo;

import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import com.bit.datainkback.enums.TaskStatus;
import com.bit.datainkback.repository.mongo.FolderRepository;
import com.bit.datainkback.repository.mongo.MongoProjectDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MongoProjectDataService {

    @Autowired
    private MongoProjectDataRepository mongoProjectDataRepository;

    @Autowired
    private FolderRepository folderRepository;

    // MongoDB에 프로젝트 데이터 생성
    public String createMongoProjectData(Long projectId) {
        // MongoDB에 프로젝트 데이터 저장
        MongoProjectData mongoProjectData = new MongoProjectData();
        mongoProjectData.setProjectId(projectId);
        mongoProjectData.setFolders(List.of());  // 폴더 구조 포함

        MongoProjectData savedData = mongoProjectDataRepository.save(mongoProjectData); // MongoDB에 저장
        return savedData.getId();  // 저장된 MongoDB ID 반환
    }

    // 프로젝트 ID로 folderIds를 조회하는 메소드
    public List<String> getFolderIdsByProjectId(Long projectId) {
        MongoProjectData mongoProjectData = mongoProjectDataRepository.findByProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return mongoProjectData.getFolders();  // 프로젝트의 folderIds 반환
    }

    public void addFolderToProject(Long projectId, Folder folder) {
        MongoProjectData mongoProjectData = mongoProjectDataRepository.findByProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("MongoProjectData not found"));

        // 폴더에 ID 할당
        assignIdToFolderAndChildren(folder);  // 모든 하위 폴더에도 ID를 할당

        // 기존 폴더 리스트에 새로운 폴더 추가
        List<String> currentFolders = mongoProjectData.getFolders();
        currentFolders.add(folder.getId());  // 새 폴더의 ID를 추가

        // MongoDB에 업데이트
        mongoProjectData.setFolders(currentFolders);
        mongoProjectDataRepository.save(mongoProjectData);  // MongoDB에 업데이트

        // Folder를 별도의 컬렉션에 저장
        folderRepository.save(folder);
    }

    // 폴더와 하위 폴더에 ID를 재귀적으로 부여하는 메서드
    private void assignIdToFolderAndChildren(Folder folder) {
        folder.generateId();  // MongoDB의 ObjectId를 사용하여 ID 생성

        // 하위 폴더(children)가 있으면 재귀적으로 ID를 할당
        if (folder.getChildren() != null) {
            for (Folder child : folder.getChildren()) {
                // 자식의 isFolder 값을 요청에서 받은 값으로 설정
                child.setFolder(child.isFolder());  // 직접 설정
                assignIdToFolderAndChildren(child);  // 하위 폴더에도 ID 할당
            }
        }
    }


    public void updateTaskStatus(String taskId, TaskStatus newStatus) {
        // 작업 상태 업데이트 로직 (BeforeLabelTask -> MongoLabelTasks 전환)
    }
}

