package com.bit.datainkback.service.impl;

import com.bit.datainkback.entity.LabelTask;
import com.bit.datainkback.enums.TaskLevel;
import com.bit.datainkback.enums.TaskStatus;
import com.bit.datainkback.repository.LabelTaskRepository;
import com.bit.datainkback.service.LabelTaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@AllArgsConstructor // 생성자 자동 생성
public class LabelTaskServiceImpl implements LabelTaskService {

    private final LabelTaskRepository labelTaskRepository;

//    @Override
//    public List<FolderDto> getFolderStructureByProjectId(String projectId) {
//        // projects 컬렉션에서 해당 projectId로 프로젝트 데이터 가져오기
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));
//
//        // 프로젝트에 속한 folders 컬렉션에서 폴더 ID를 통해 폴더 구조 조회
//        List<Folder> folders = folderRepository.findByIdIn(project.getFolders());
//
//        // 폴더 데이터를 트리 구조로 변환
//        return convertToTreeStructure(folders);
//    }
//
//    private List<FolderDto> convertToTreeStructure(List<Folder> folders) {
//        // 폴더 트리 변환 로직 구현
//        // 필요한 경우 checkable 속성 추가
//    }


    @Override
    public void rejectLabelTask(Long taskId, String rejectionReason, Timestamp reviewedTimestamp) {
        LabelTask labelTask = labelTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // **필드 이름을 수정**
        labelTask.setRejectionReason(rejectionReason); // 수정된 부분
        labelTask.setReviewed(reviewedTimestamp);
        labelTask.setStatus(TaskStatus.IN_PROGRESS);
        labelTask.setLevel(TaskLevel.LABELER);

        labelTaskRepository.save(labelTask);
    }

    @Override
    public void approveLabelTask(Long taskId, String comment, Timestamp reviewedTimestamp) {
        LabelTask labelTask = labelTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        labelTask.setComment(comment);
        labelTask.setStatus(TaskStatus.REVIEWED);
        labelTask.setReviewed(reviewedTimestamp);

        labelTaskRepository.save(labelTask);
    }
}
