package com.bit.datainkback.dto;

import com.bit.datainkback.entity.SourceData;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SourceDataDto {
    private Long sourceId;
    private Long projectId;
    private String fileUrl;
    private String fileName;
    private Timestamp uploadTime;

    public SourceData toEntity() {
        return SourceData.builder()
                .sourceId(this.sourceId)
                .projectId(this.projectId)
                .fileUrl(this.fileUrl)
                .fileName(this.fileName)
                .uploadTime(this.uploadTime)
                .build();
    }
}
