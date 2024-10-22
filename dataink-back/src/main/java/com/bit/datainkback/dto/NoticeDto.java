package com.bit.datainkback.dto;

import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.entity.User;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    private String name;
    private Timestamp created; // 작성 시간
    private Timestamp moddate; // 작성 시간
    private String searchKeyword;
    private String searchCondition;
    private List<NoticeFileDto> noticeFileDtoList;

    public Notice toEntity(User user) {
        return Notice.builder()
                .noticeId(this.noticeId)
                .title(this.title)
                .content(this.content)
                .user(user)
                .created(this.created)
                .moddate(this.moddate)
                .noticeFileList(new ArrayList<>())
                .build();
    }

}
