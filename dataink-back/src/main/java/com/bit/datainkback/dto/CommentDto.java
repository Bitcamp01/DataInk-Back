package com.bit.datainkback.dto;

import com.bit.datainkback.entity.Comment;
import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommentDto {
    private Long commentId;
    private Long userId;
    private Long noticeId;
    private String content;

    public Comment toEntity(User user, Notice notice) {
        return Comment.builder()
                .commentId(this.commentId)
                .user(user)
                .notice(notice)
                .content(this.content)
                .build();
    }
}
