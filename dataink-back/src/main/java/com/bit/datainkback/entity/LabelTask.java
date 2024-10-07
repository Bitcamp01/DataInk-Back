package com.bit.datainkback.entity;

import com.bit.datainkback.dto.LabelTaskDto;
import com.bit.datainkback.enums.TaskLevel;
import com.bit.datainkback.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "LABEL_TASK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "project_id")
//    private Project project;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskLevel level;
    private String comment;

    @Column(name = "rejection_reason")
    private String rejectReason;
    private LocalDateTime create;
    private LocalDateTime update;
    private LocalDateTime submit;
    private LocalDateTime review;
    private LocalDateTime approve;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private SourceData sourceData;

//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "field_id")
//    private LabelField labelField;

    public LabelTaskDto toDto() {
        return LabelTaskDto.builder()
                .taskId(this.taskId)
//                .projectId(this.project.getProjectId())
//                .userId(this.user.getUserId())
                .status(this.status)
                .level(this.level)
                .comment(this.comment)
                .rejectionReason(this.rejectReason)
                .create(this.create)
                .update(this.update)
                .submit(this.submit)
                .review(this.review)
                .approve(this.approve)
                .sourceDataId(this.sourceData.getSourceId())
//                .labelFieldId(this.labelField.getFieldId())
                .build();
    }

}
