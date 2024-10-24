package com.bit.datainkback.entity;

import com.bit.datainkback.dto.CalendarDto;
import com.bit.datainkback.dto.EventDto;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@SequenceGenerator(
        name = "eventSeqGenerator",
        sequenceName = "EVENT_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "eventSeqGenerator"
    )
    @Column(name = "event_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    @Column(name = "title", nullable = false, length = 500)
    private String title;
    @Column(name = "start_date", nullable = false)
    private Timestamp startDate;
    @Column(name = "end_date", nullable = false)
    private Timestamp endDate;
    @Column(name = "memo", length = 500)
    private String memo;

    // 캘린더로부터 색상 정보 참조
    @Transient // 이 필드는 데이터베이스에 매핑되지 않음
    public String getColor() {
        return calendar != null ? calendar.getColor() : "#FFFFFF"; // 캘린더 색상이 없으면 기본값 반환
    }

    public EventDto toDto() {
        return EventDto.builder()
                .id(this.id)
                .userId(this.user.getUserId())
                .calendarId(this.calendar.getId())
                .title(this.title)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .memo(this.memo)
                .color(getColor()) // 캘린더 색상 설정
                .build();
    }

}

