package com.bit.datainkback.service;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.dto.RejectReasonDto;
import com.bit.datainkback.entity.mongo.Field;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
public interface LabelTaskService {

//    // 반려 시
    void rejectLabelTask(String taskId, String rejectionReason, Map<String, Object> transformedData);
//    // 승인 시
    void approveLabelTask(String taskId, String comment, Map<String, Object> transformedData);

    List<LabelTaskDto> getAllLabelTasks();

    LabelTaskDto getLabelTaskById(Long taskId);

    List<Field> getLabelTaskDetails(String taskId);
}
