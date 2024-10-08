package com.bit.datainkback.entity;

import com.bit.datainkback.dto.TempTaskDto;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@SequenceGenerator(
        name = "tempTaskSeqGenerator",
        sequenceName = "TEMPTASK_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(name = "TEMP_TASK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempTask {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator ="projectSeqGenerator"
    )
    @Column(name = "temp_id")
    private Long tempId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "temp_content", nullable = false)
    private String tempContent;

    @Column(name = "last_saved", nullable = false)
    private Timestamp lastSaved;

    public TempTaskDto toDto() {
        return TempTaskDto.builder()
                .tempId(this.tempId)
                .projectId(this.project.getProjectId())
                .tempContent(this.tempContent)
                .lastSaved(this.lastSaved)
                .build();
    }
}
