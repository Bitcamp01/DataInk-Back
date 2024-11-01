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
    @JoinColumn(name = "labeler_id")
    private User labeler;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    private String comment;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private SourceData sourceData;

//    // **LabelField 대신 Tasks의 ID를 필드로 사용**
//    @Column(name = "field_id")
//    private String fieldId; // MongoDB의 Tasks 문서의 ID를 저장

    @Column(name = "ref_task_id")
    private String refTaskId;

    public LabelTaskDto toDto() {
        return LabelTaskDto.builder()
                .taskId(this.taskId)
                .laberId(this.labeler.getUserId())
                .reviewerId(this.reviewer.getUserId())
                .adminId(this.admin.getUserId())
                .comment(this.comment)
                .rejectionReason(this.rejectionReason)
                .sourceDataId(this.sourceData.getSourceId())
                .refTaskId(this.refTaskId)
                .build();
    }

}