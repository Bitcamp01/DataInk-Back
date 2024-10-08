package com.bit.datainkback.dto;

import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserProject;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserProjectDto {
    private Long userId;
    private Long projectId;
    private Enum role;
    private int userWorkcnt;
    private int totalWorkcnt;
    private int pendingInspection;
    private int completedInspection;
    private Timestamp endDate;

    public UserProject toEntity(User user, Project project) {
        return UserProject.builder()
                .user(user)
                .project(project)
                .role(this.role)
                .userWorkcnt(this.userWorkcnt)
                .totalWorkcnt(this.totalWorkcnt)
                .pendingInspection(this.pendingInspection)
                .completedInspection(this.completedInspection)
                .endDate(endDate)
                .build();
    }

}
