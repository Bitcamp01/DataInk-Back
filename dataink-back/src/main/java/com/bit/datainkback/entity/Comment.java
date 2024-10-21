package com.bit.datainkback.entity;

import com.bit.datainkback.dto.CommentDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "commentSeqGenerator",
        sequenceName = "COMMENT_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "commentSeqGenerator"
    )
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user; // User 엔티티와의 관계 설정

    @ManyToOne
    @JoinColumn(name = "notice_id",  referencedColumnName = "notice_id", nullable = false)
    private Notice notice;

    @Column(nullable = false)
    private String content;

    public CommentDto toDto() {
        return CommentDto.builder()
                .commentId(this.commentId)
                .userId(this.user.getUserId())
                .noticeId(this.notice.getNoticeId())
                .content(this.content)
                .build();
    }
}
