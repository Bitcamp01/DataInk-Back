package com.bit.datainkback.entity;

import com.bit.datainkback.dto.SourceDataDto;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "SOURCE_DATA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "source_data_seq_generator",
        sequenceName = "source_data_seq", // DB 시퀀스 이름
        initialValue = 1,
        allocationSize = 1
)
public class SourceData {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "source_data_seq_generator"
    )
    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "upload_time", nullable = false)
    private Timestamp uploadTime;

    // toDto() 메서드
    public SourceDataDto toDto() {
        return SourceDataDto.builder()
                .sourceId(this.sourceId)
                .projectId(this.projectId)
                .fileUrl(this.fileUrl)
                .fileName(this.fileName)
                .uploadTime(this.uploadTime)
                .build();
    }
}
