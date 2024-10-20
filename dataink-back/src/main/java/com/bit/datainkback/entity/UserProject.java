package com.bit.datainkback.entity;

import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.enums.UserRole;
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

    @Enumerated(EnumType.STRING)
    private UserRole role; // 역할 Enum

    @Column(name = "user_worknt")
    private int userWorkcnt; // 작업 유형

    @Column(name = "total_worknt")
    private int totalWorkcnt; // 총 작업 수

    @Column(name = "pending_inspection")
    private int pendingInspection; // 검사 대기 수

    @Column(name = "completed_inspection")
    private int completedInspection; // 완료된 검사 수

    public UserProjectDto toDto() {
        return UserProjectDto.builder()
                .userId(this.id.getUser().getUserId())  // userId는 Long 타입
                .projectId(this.id.getProject().getProjectId())
                .role(this.role)
                .userWorkcnt(this.userWorkcnt)
                .totalWorkcnt(this.totalWorkcnt)
                .pendingInspection(this.pendingInspection)
                .completedInspection(this.completedInspection)
                .build();
    }
}
