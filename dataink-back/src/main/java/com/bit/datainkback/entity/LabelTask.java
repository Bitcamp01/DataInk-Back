package com.bit.datainkback.entity;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.enums.TaskLevel;
import com.bit.datainkback.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@SequenceGenerator(
        name = "labelTaskSeqGenerator",
        sequenceName = "LABELTASK_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(name = "LABEL_TASK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelTask {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "labelTaskSeqGenerator"
    )
    @Column(name = "task_id")
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskLevel level;
    private String comment;

    @Column(name = "rejection_reason")
    private String rejectionReason;
    private Timestamp created;
    private Timestamp updated;
    private Timestamp submitted;
    private Timestamp reviewed;
    private Timestamp approved;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private SourceData sourceData;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "field_id")
    private LabelField labelField;

    public LabelTaskDto toDto() {
        return LabelTaskDto.builder()
                .taskId(this.taskId)
                .projectId(this.project.getProjectId())
//                .userId(this.user.getUserId())
                .status(this.status)
                .level(this.level)
                .comment(this.comment)
                .rejectionReason(this.rejectionReason)
                .created(this.created)
                .updated(this.updated)
                .submitted(this.submitted)
                .reviewed(this.reviewed)
                .approved(this.approved)
                .sourceDataId(this.sourceData.getSourceId())
                .labelFieldId(this.labelField.getFieldId())
                .build();
    }

}
