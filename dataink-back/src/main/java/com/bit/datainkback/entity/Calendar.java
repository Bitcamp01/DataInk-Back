package com.bit.datainkback.entity;

import com.bit.datainkback.dto.CalendarDto;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@SequenceGenerator(
        name = "calendarSeqGenerator",
        sequenceName = "CALENDAR_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calendar {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "calendarSeqGenerator"
    )
    @Column(name = "calendar_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "calendar_name", nullable = false, length = 500)
    private String calendarName;
    @Column(name = "color", nullable = false, length = 500)
    private String color;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;

    public CalendarDto toDto() {
        return CalendarDto.builder()
                .id(this.id)
                .userId(this.user.getUserId())
                .calendarName(this.calendarName)
                .color(this.color)
                .build();
    }

}
