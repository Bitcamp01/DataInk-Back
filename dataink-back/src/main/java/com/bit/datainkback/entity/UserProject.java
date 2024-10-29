package com.bit.datainkback.entity;

import com.bit.datainkback.dto.UserProjectDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProject {
    @EmbeddedId
    private UserProjectId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("projectId")
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "user_worknt")
    private int userWorkcnt; // 작업 유형

    @Column(name = "total_worknt")
    private int totalWorkcnt; // 총 작업 수

    @Column(name = "pending_inspection")
    private int pendingInspection; // 검사 대기 수

    @Column(name = "completed_inspection")
    private int completedInspection; // 완료된 검사 수

    @Column(name = "is_bookmarked", nullable = false)
    private boolean isBookmarked = false; // 기본값 false로 설정

    public UserProjectDto toDto() {
        return UserProjectDto.builder()
                .userId(this.id.getUserId())  // userId는 Long 타입
                .projectId(this.id.getProjectId())
                .userWorkcnt(this.userWorkcnt)
                .totalWorkcnt(this.totalWorkcnt)
                .pendingInspection(this.pendingInspection)
                .completedInspection(this.completedInspection)
                .isBookmarked(this.isBookmarked)
                .build();
    }
}
