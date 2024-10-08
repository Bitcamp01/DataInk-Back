package com.bit.datainkback.service;

import com.bit.datainkback.dto.RejectReasonDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LabelTaskService {
    <LabelTaskDTO> List<LabelTaskDTO> getAllTasks();

    <LabelTaskDTO> LabelTaskDTO getTaskById(Long taskId);

    void approveTask(Long taskId);

    void rejectTask(Long taskId, RejectReasonDto reasonDto);
}
