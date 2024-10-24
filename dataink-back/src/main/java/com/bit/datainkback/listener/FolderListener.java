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
        // Optional로 기존 task를 찾아본다
        Optional<Tasks> existingTaskOpt = taskRepository.findByParentFolderIdAndTaskName(parentFolderId, file.getLabel());

        Tasks tasks;
        if (existingTaskOpt.isPresent()) {
            // 이미 존재하는 task가 있다면 그 task를 사용
            tasks = existingTaskOpt.get();
        } else {
            // 없으면 새로 생성
            tasks = new Tasks();
            tasks.setParentFolderId(parentFolderId);
        }

        // Optional로 부모 폴더의 itemIds 가져오기
        Optional<Folder> parentFolderOpt = folderRepository.findById(parentFolderId);
        List<String> itemIds = file.getItemIds();

        if ((itemIds == null || itemIds.isEmpty()) && parentFolderOpt.isPresent()) {
            itemIds = parentFolderOpt.get().getItemIds();  // 부모 폴더의 itemIds를 상속
        }

        tasks.setItemIds(itemIds);
        tasks.setTaskName(file.getLabel());  // 파일의 label을 task 이름으로 사용
        tasks.setStatus("in_progress");
        tasks.setFieldValue(new HashMap<>());  // 기본 필드 값

        // Task를 저장 (새로 생성이든 업데이트든)
        taskRepository.save(tasks);

        // 만약 폴더 내부에 또 다른 폴더/파일이 있으면 재귀적으로 처리
        if (file.getChildren() != null) {
            for (Folder child : file.getChildren()) {
                moveTasksFromFolder(child, file.getId());  // 자식 폴더/파일도 처리
            }
        }
    }


}
