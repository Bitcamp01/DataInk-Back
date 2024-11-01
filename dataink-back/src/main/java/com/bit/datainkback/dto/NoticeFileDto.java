package com.bit.datainkback.dto;


import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.entity.NoticeFile;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NoticeFileDto {
    private Long fileId;
    private Long noticeId;
    private String fileName;
    private String filePath;
    private String fileOriginName;
    private String fileType;
    private String fileStatus;
    private String fileNewName;
    @JsonProperty("fileSize")
    private Long fileSize;

    public NoticeFile toEntity(Notice notice) {
        return NoticeFile.builder()
            .fileId(this.fileId)
            .notice(notice)
            .fileName(this.fileName)
            .fileOriginName(this.fileOriginName)
            .filePath(filePath)
            .fileType(fileType)
            .fileStatus(this.fileStatus)
            .fileNewName(this.fileNewName)
            .fileSize(this.fileSize)
            .build();
    }

}
