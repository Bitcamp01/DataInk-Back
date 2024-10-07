package com.bit.datainkback.dto;

import com.bit.datainkback.enums.TaskLevel;
import com.bit.datainkback.enums.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime create;
    private LocalDateTime update;
    private LocalDateTime submit;
    private LocalDateTime review;
    private LocalDateTime approve;
    private Long sourceDataId;
    private Long labelFieldId;
}
