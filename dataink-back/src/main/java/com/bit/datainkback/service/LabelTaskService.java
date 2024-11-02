package com.bit.datainkback.service;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.mongo.Field;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
public interface LabelTaskService {

//    // 반려 시
    void rejectLabelTask(String taskId, String rejectionReason, Map<String, Object> transformedData, User joinedUser);
//    // 승인 시
    void approveLabelTask(String taskId, String comment, Map<String, Object> transformedData, User joinedUser);

    List<LabelTaskDto> getAllLabelTasks();

    LabelTaskDto getLabelTaskById(Long taskId);

    List<Field> getLabelTaskDetails(String taskId);

    List<Object> getLabelDetails(String taskId);

    void saveLabelDetail(String taskId, Map<String, Object> transformedData, User joinedUser);

    void adminApprove(String taskId, Map<String, Object> transformedData, User joinedUser);

    void createNewTask(String id);
}
