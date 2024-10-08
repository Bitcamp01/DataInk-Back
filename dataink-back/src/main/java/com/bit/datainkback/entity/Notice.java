package com.bit.datainkback.entity;

import com.bit.datainkback.dto.NoticeDto;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@SequenceGenerator(
        name = "noticeSeqGenerator",
        sequenceName = "NOTICE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "noticeSeqGenerator"
    )
    @Column(name = "notice_id")
    private Long noticeId;
    @Column(nullable = false)
    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user; // User 엔티티와의 관계 설정

    @Column(nullable = false)
    private Timestamp created;


    public NoticeDto toDto() {
        return NoticeDto.builder()
                .noticeId(this.noticeId)
                .title(this.title)
                .content(this.content)
                .userId(this.user.getUserId())
                .created(this.created)
                .build();
    }
}
