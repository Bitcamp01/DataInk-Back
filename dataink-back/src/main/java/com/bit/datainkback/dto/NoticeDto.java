package com.bit.datainkback.dto;

import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.entity.User;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NoticeDto {
    private Long noticeId; // 공지사항 ID
    private String title; // 제목
    private String content; // 내용
    private Long userId; // 작성자 ID
    private Timestamp created; // 작성 시간
    private String uploadFile; // 업로드 파일

    public Notice toEntity(User user) {
        return Notice.builder()
                .noticeId(this.noticeId)
                .title(this.title)
                .content(this.content)
                .user(user)
                .created(this.created)
                .uploadFile(this.uploadFile)
                .build();
    }

}