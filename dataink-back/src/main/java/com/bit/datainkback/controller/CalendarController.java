package com.bit.datainkback.controller;

import com.bit.datainkback.dto.CalendarDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendars")
@RequiredArgsConstructor
@Slf4j
public class CalendarController {
    private final CalendarService calendarService;

    @PostMapping
    public ResponseEntity<CalendarDto> createCalendar(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody CalendarDto calendarDto) {
        // 로그인된 사용자 ID 가져오기
        Long loggedInUserId = customUserDetails.getUser().getUserId();
        // 캘린더 생성
        CalendarDto createdCalendar = calendarService.createCalendar(loggedInUserId, calendarDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCalendar);
    }

    @GetMapping
    public ResponseEntity<List<CalendarDto>> getAllCalendars(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 사용자 ID로 모든 캘린더 조회
        Long loggedInUserId = customUserDetails.getUser().getUserId();
        List<CalendarDto> calendars = calendarService.getCalendarsByUserId(loggedInUserId);
        return ResponseEntity.ok(calendars);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalendarDto> updateCalendar(@PathVariable("id") Long id, @RequestBody CalendarDto calendarDto) {
        // 캘린더 업데이트
        CalendarDto updatedCalendar = calendarService.updateCalendar(id, calendarDto);
        return ResponseEntity.ok(updatedCalendar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable("id") Long id) {
        // 캘린더 삭제
        calendarService.deleteCalendar(id);
        return ResponseEntity.noContent().build();
    }
}
