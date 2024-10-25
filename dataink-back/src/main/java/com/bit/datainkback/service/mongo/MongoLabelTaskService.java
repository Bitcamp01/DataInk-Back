package com.bit.datainkback.service.mongo;

import com.bit.datainkback.dto.mongo.TaskSearchCriteria;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    // 조건으로 검색 기능 (StatusIn이 아니라 Status 아닌가?)
    public List<String> getFolderIdsBasedOnCategories(TaskSearchCriteria criteria, List<Folder> folderItems) {
        List<String> folderIds = new ArrayList<>();

        // 폴더 구조를 탐색하여 선택된 카테고리에 맞는 폴더 ID 수집
        for (Folder folder : folderItems) {
            collectMatchingFolderIds(folder, criteria, folderIds, new ArrayList<>());
        }

        return folderIds;
    }

    // 폴더 구조 탐색 함수
    private void collectMatchingFolderIds(Folder folder, TaskSearchCriteria criteria, List<String> folderIds, List<String> parentLabels) {
        List<String> updatedParentLabels = new ArrayList<>(parentLabels);
        updatedParentLabels.add(folder.getLabel());  // 현재 폴더의 label 추가

        // 카테고리 조건에 맞는지 확인
        boolean matchesCategory1 = (criteria.getCategory1() == null || criteria.getCategory1().isEmpty()) || (updatedParentLabels.size() >= 1 && updatedParentLabels.get(0).equals(criteria.getCategory1()));
        boolean matchesCategory2 = (criteria.getCategory2() == null || criteria.getCategory2().isEmpty()) || (updatedParentLabels.size() >= 2 && updatedParentLabels.get(1).equals(criteria.getCategory2()));
        boolean matchesCategory3 = (criteria.getCategory3() == null || criteria.getCategory3().isEmpty()) || (updatedParentLabels.size() >= 3 && updatedParentLabels.get(2).equals(criteria.getCategory3()));

        if (matchesCategory1 && matchesCategory2 && matchesCategory3) {
            // 현재 폴더 ID 추가
            if (folder.isFolder()) {
                folderIds.add(folder.getId());
            }
        }

        // 하위 폴더가 있는 경우 재귀적으로 탐색
        if (folder.getChildren() != null && !folder.getChildren().isEmpty()) {
            for (Folder childFolder : folder.getChildren()) {
                collectMatchingFolderIds(childFolder, criteria, folderIds, updatedParentLabels);
            }
        }
    }

    public List<Tasks> searchTasks(List<String> folderIds, String taskName, String status) {
        // 폴더 ID와 검색어, 작업 상태에 맞는 Task 검색
        return mongoLabelTaskRepository.findTasksByFolderIdsAndTaskNameAndStatus(folderIds, taskName, status);
    }
}
