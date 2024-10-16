package com.bit.datainkback.listener;

import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FolderListener extends AbstractMongoEventListener<Folder> {

    private final MongoLabelTaskRepository taskRepository;

    @Autowired
    public FolderListener(MongoLabelTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<Folder> event) {
        Folder folder = event.getSource();
        processFolder(folder);  // 저장된 폴더 처리
    }

    private void processFolder(Folder folder) {
        // 폴더 내부에 자식 폴더 또는 파일이 있을 경우 처리
        if (folder.getChildren() != null) {
            for (Folder child : folder.getChildren()) {
                if (!child.isFolder()) {
                    // isFolder가 false일 경우 파일로 간주하고 Task로 이동
                    moveTasksFromFolder(child, folder.getId());
                } else {
                    processFolder(child);  // 자식 폴더 재귀 처리
                }
            }
        }
    }

    private void moveTasksFromFolder(Folder file, String parentFolderId) {
        // 파일을 Task로 변환하여 Task 컬렉션에 저장
        Tasks tasks = new Tasks();
        tasks.setParentFolderId(parentFolderId);
        tasks.setItemIds(file.getItemIds());
        tasks.setTaskName(file.getLabel());
        tasks.setStatus("in_progress");
        tasks.setFieldValue(new HashMap<>());  // 기본 필드 값 설정

        taskRepository.save(tasks);  // Task 컬렉션에 저장
    }
}
