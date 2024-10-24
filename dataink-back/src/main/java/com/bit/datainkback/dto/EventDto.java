package com.bit.datainkback.dto;

import com.bit.datainkback.entity.Calendar;
import com.bit.datainkback.entity.Event;
import com.bit.datainkback.entity.User;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EventDto {
    private Long id;
    private Long userId;
    private Long calendarId;
    private String title;
    private Timestamp startDate;
    private Timestamp endDate;
    private String memo;
    private String color;      // 이벤트 색상 추가

    public Event toEntity(User user, Calendar calendar) {
        return Event.builder()
                .id(this.id)
                .user(user)
                .calendar(calendar)
                .title(this.title)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .memo(this.memo)
                .build();
    }
}
