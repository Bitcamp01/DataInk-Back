package com.bit.datainkback.dto;

import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.TempTask;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempTaskDto {

    private Long tempId;
    private Long projectId;  // Project 엔티티의 projectId를 가져올 예정
    private String tempContent;
    private Timestamp lastSaved;

    public TempTask toEntity(Project project) {
        return TempTask.builder()
                .tempId(this.tempId)
                .project(project)
                .tempContent(this.tempContent)
                .lastSaved(this.lastSaved)
                .build();
    }
}
