package com.bit.datainkback.service;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.dto.RejectReasonDto;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public interface LabelTaskService {

    // 반려 시
    void rejectLabelTask(Long taskId, String rejectionReason, Timestamp reviewedTimestamp);
    // 승인 시
    void approveLabelTask(Long taskId, String comment, Timestamp reviewedTimestamp);

    List<LabelTaskDto> getAllLabelTasks();
}
