package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.entity.LabelTask;
import com.bit.datainkback.enums.TaskLevel;
import com.bit.datainkback.enums.TaskStatus;
import com.bit.datainkback.repository.LabelTaskRepository;
import com.bit.datainkback.service.LabelTaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor // 생성자 자동 생성
public class LabelTaskServiceImpl implements LabelTaskService {

    private final LabelTaskRepository labelTaskRepository;


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

    @Override
    public List<LabelTaskDto> getAllLabelTasks() {
        // LabelTask 엔티티를 LabelTaskDto로 변환하여 반환
        return labelTaskRepository.findAll().stream()
                .map(LabelTask::toDto) // 엔티티의 toDto 메서드 사용
                .collect(Collectors.toList());
    }
}
