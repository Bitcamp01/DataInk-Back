package com.bit.datainkback.dto;

import com.bit.datainkback.entity.Calendar;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CalendarDto {
    private Long id;
    private Long userId;
    private String calendarName;
    private String color;


    public Calendar toEntity(User user) {
        return Calendar.builder()
                .id(this.id)
                .user(user)
                .calendarName(this.calendarName)
                .color(this.color)
                .build();
    }
}
