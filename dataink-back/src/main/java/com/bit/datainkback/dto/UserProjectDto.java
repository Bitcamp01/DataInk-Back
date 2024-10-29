package com.bit.datainkback.dto;

import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserProject;
import com.bit.datainkback.entity.UserProjectId;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private int userWorkcnt;
    private int totalWorkcnt;
    private int pendingInspection;
    private int completedInspection;

    private ProjectDto projectDto;

    @JsonProperty("isBookmarked")
    private boolean isBookmarked;

    public UserProject toEntity(User user, Project project) {
        UserProjectId userProjectId = new UserProjectId(user.getUserId(), project.getProjectId()); // 복합 키 생성

        return UserProject.builder()
                .id(userProjectId) // 복합 키 설정
                .userWorkcnt(this.userWorkcnt)
                .totalWorkcnt(this.totalWorkcnt)
                .pendingInspection(this.pendingInspection)
                .completedInspection(this.completedInspection)
                .isBookmarked(this.isBookmarked)
                .build();
    }
}
