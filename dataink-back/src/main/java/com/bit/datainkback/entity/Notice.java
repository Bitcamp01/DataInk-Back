package com.bit.datainkback.entity;

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
    @Column(name = "notice_id", unique = true, nullable = false)
    private int noticeId;

    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user; // User 엔티티와의 관계 설정

    private Timestamp created;

    @Column(name = "upload_file")
    private String uploadFile;

    public NoticeDto toDto() {
        return NoticeDto.builder()
                .noticeId(this.noticeId)
                .title(this.title)
                .cnotent(this.content)
                .user(this.getUser())
    }
}
