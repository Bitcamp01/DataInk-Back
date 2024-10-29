package com.bit.datainkback.service;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.dto.RejectReasonDto;
import com.bit.datainkback.entity.mongo.Field;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public interface LabelTaskService {

    // 반려 시
    void rejectLabelTask(String taskId, String refTaskId, String rejectionReason);
    // 승인 시
    void approveLabelTask(String taskId, String refTaskId, String comment);

    List<LabelTaskDto> getAllLabelTasks();

    LabelTaskDto getLabelTaskById(Long taskId);

    List<Field> getLabelTaskDetails(String taskId);
}
