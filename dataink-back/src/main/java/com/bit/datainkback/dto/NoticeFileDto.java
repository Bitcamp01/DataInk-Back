package com.bit.datainkback.dto;


import com.bit.datainkback.entity.Notice;
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

    public NoticeFileDto toEntity(Notice notice) {
        return NoticeFileDto.builder()
            .fileId(this.fileId)
            .noticeId(this.noticeId)
            .fileName(this.fileName)
            .fileOriginName(this.fileOriginName)
            .filePath(filePath)
            .fileType(fileType)
            .fileStatus(this.fileStatus)
            .fileNewName(this.fileNewName)
            .build();

    }
}
