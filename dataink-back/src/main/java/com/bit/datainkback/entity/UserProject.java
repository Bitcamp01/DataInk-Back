package com.bit.datainkback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@SequenceGenerator(
        name = "userProjectSeqGenerator",
        sequenceName = "USERPROJECT_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProject {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "userProjectSeqGenerator"
    )
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user; // User와의 관계 설정

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "prject_id", nullable = false)
    private Project project;

    private Enum role;

    @Column(name = "user_worknt")
    private int userWorkcnt; // 작업 유형

    @Column(name = "total_worknt")
    private int totalWorkcnt; // 총 작업 수

    @Column(name = "pending_inspection")
    private int pendingInspection; // 검사 대기 수

    @Column(name = "completed_inspection")
    private int completedInspection; // 완료된 검사 수

    @Column(name = "end_date")
    private Timestamp endDate; // 종료 날짜


}
