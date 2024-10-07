package com.bit.datainkback.dto;

import com.bit.datainkback.entity.LabelTask;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.SourceData;
import com.bit.datainkback.entity.User;
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

//    public LabelTask toEntity(Project project, User user, SourceData sourceData) {
//        return LabelTask.builder()
//                .taskId(this.taskId)
//                .project(project)
//                .user(user)
//                .status(this.status)
//                .level(this.level)
//                .comment(this.comment)
//                .rejectionReason(this.rejectionReason)
//                .createdAt(this.create)
//                .updatedAt(this.update)
//                .submittedAt(this.submit)
//                .reviewedAt(this.review)
//                .approvedAt(this.approve)
//                .sourceData(sourceData)
//                .build();
//    }
}
