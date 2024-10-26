package com.bit.datainkback.service.mongo;

import com.bit.datainkback.dto.mongo.TaskSearchCriteria;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MongoLabelTaskService {

    @Autowired
    private MongoLabelTaskRepository mongoLabelTaskRepository;

    private final MongoTemplate mongoTemplate;

    public MongoLabelTaskService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

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

    // 폴더 ID 수집 함수
    public List<String> getFolderIdsBasedOnCategories(TaskSearchCriteria criteria, List<Folder> folderItems) {
        List<String> folderIds = new ArrayList<>();
        for (Folder folder : folderItems) {
            collectMatchingFolderIds(folder, criteria, folderIds, new ArrayList<>());
        }
        return folderIds;
    }

    // 폴더 구조 탐색 함수
    private void collectMatchingFolderIds(Folder folder, TaskSearchCriteria criteria, List<String> folderIds, List<String> parentLabels) {
        List<String> updatedParentLabels = new ArrayList<>(parentLabels);
        updatedParentLabels.add(folder.getLabel());  // 현재 폴더의 label 추가

        // 카테고리 조건 확인
        boolean matchesCategory1 = (criteria.getCategory1() == null || criteria.getCategory1().isEmpty()) ||
                (updatedParentLabels.size() >= 1 && updatedParentLabels.get(0).equals(criteria.getCategory1()));

        boolean matchesCategory2 = (criteria.getCategory2() == null || criteria.getCategory2().isEmpty()) ||
                (updatedParentLabels.size() >= 2 && updatedParentLabels.get(1).equals(criteria.getCategory2()));

        boolean matchesCategory3 = (criteria.getCategory3() == null || criteria.getCategory3().isEmpty()) ||
                (updatedParentLabels.size() >= 3 && updatedParentLabels.get(2).equals(criteria.getCategory3()));

        if (matchesCategory1 && matchesCategory2 && matchesCategory3) {
            folderIds.add(folder.getId());
        }

        // 하위 폴더 탐색
        if (folder.getChildren() != null) {
            for (Folder childFolder : folder.getChildren()) {
                collectMatchingFolderIds(childFolder, criteria, folderIds, updatedParentLabels);
            }
        }
    }

    // Task 검색 함수
    public List<Tasks> searchTasks(List<String> folderIds, String taskName, String status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("parentFolderId").in(folderIds));

        if (taskName != null && !taskName.isEmpty()) {
            query.addCriteria(Criteria.where("taskName").regex(taskName, "i"));  // 대소문자 구분 없이 검색
        }

        if (status != null && !status.isEmpty()) {
            query.addCriteria(Criteria.where("status").is(status));
        }

        return mongoTemplate.find(query, Tasks.class);
    }
}
