package com.bit.datainkback.service;

import com.bit.datainkback.dto.CalendarDto;

import java.util.List;

public interface CalendarService {

    CalendarDto createCalendar(Long loggindUserId, CalendarDto calendarDto);

    List<CalendarDto> getCalendarsByUserId(Long userId);

    CalendarDto updateCalendar(Long id, CalendarDto calendarDto);

    void deleteCalendar(Long id);
}
