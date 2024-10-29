package com.bit.datainkback.dto;

import com.bit.datainkback.entity.LabelTask;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.SourceData;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.enums.TaskLevel;
import com.bit.datainkback.enums.TaskStatus;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LabelTaskDto {
    private Long taskId;
    private Long projectId;
    private Long userId;
    private TaskStatus status;
    private TaskLevel level;
    private String comment;
    private String rejectionReason;
    private Timestamp created;
    private Timestamp updated;
    private Timestamp submitted;
    private Timestamp reviewed;
    private Timestamp approved;
    private Long sourceDataId;
    private Long labelFieldId;
    private String refTaskId;

}
