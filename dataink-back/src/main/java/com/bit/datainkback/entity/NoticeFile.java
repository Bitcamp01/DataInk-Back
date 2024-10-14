package com.bit.datainkback.entity;


import com.bit.datainkback.dto.NoticeFileDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name="noticeFileSeqGenerator",
        sequenceName = "NOTICE_FILE_SEQ",
        initialValue = 1,
        allocationSize = 1
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeFile {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "noticeFileSeqGenerator"
    )

    @Column(name = "file_id")
    private Long fileId;

    @ManyToOne
    @JoinColumn(name="notice_id", referencedColumnName = "notice_id")
    @JsonBackReference
    private Notice notice;

    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_origin_name")
    private String fileOriginName;
    @Column(name = "file_path")
    private String filePath;
    @Column(name = "file_type")
    private String fileType;
    @Transient
    private String fileStatus;
    @Transient
    private String fileNewName;

    public NoticeFileDto toDto(){
        return NoticeFileDto.builder()
                .fileId(this.fileId)
                .noticeId(this.notice.getNoticeId())
                .fileName(this.fileName)
                .fileOriginName(this.fileOriginName)
                .filePath(filePath)
                .fileType(fileType)
                .fileStatus(fileStatus)
                .fileNewName(fileNewName)
                .build();
    }

}
