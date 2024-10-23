package com.bit.datainkback.dto;


import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.entity.NoticeFile;
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
            .build();

    }
}
