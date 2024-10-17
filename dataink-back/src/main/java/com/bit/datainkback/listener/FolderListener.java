package com.bit.datainkback.listener;

import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.repository.mongo.FolderRepository;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class FolderListener extends AbstractMongoEventListener<Folder> {

    private final MongoLabelTaskRepository taskRepository;
    private final FolderRepository folderRepository;

    @Autowired
    public FolderListener(MongoLabelTaskRepository taskRepository, FolderRepository folderRepository) {
        this.taskRepository = taskRepository;
        this.folderRepository = folderRepository;
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
        Tasks tasks = new Tasks();
        tasks.setParentFolderId(parentFolderId);

        // Optional을 안전하게 처리하여 parentFolderId에 대한 값을 가져옴
        Optional<Folder> parentFolderOpt = folderRepository.findById(parentFolderId);
        List<String> itemIds = file.getItemIds();

        if ((itemIds == null || itemIds.isEmpty()) && parentFolderOpt.isPresent()) {
            itemIds = parentFolderOpt.get().getItemIds();  // 부모 폴더의 itemIds를 상속
        }

        tasks.setItemIds(itemIds);
        tasks.setTaskName(file.getLabel());
        tasks.setStatus("in_progress");
        tasks.setFieldValue(new HashMap<>());  // 기본 필드 값

        taskRepository.save(tasks);  // Task 컬렉션에 저장
    }

}
