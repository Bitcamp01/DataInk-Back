package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.CalendarDto;
import com.bit.datainkback.entity.Calendar;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.CalendarRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    @Override
    public CalendarDto createCalendar(Long loggedInUserId, CalendarDto calendarDto) {
        // 사용자 조회
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 캘린더 엔티티 생성
        Calendar calendar = calendarDto.toEntity(user);
        Calendar savedCalendar = calendarRepository.save(calendar);

        // 저장된 캘린더를 DTO로 변환하여 반환
        return savedCalendar.toDto();
    }

    @Override
    public List<CalendarDto> getCalendarsByUserId(Long userId) {
        // 사용자 ID로 캘린더 목록 조회
        List<Calendar> calendars = calendarRepository.findByUser_UserId(userId);

        // 캘린더 목록을 DTO 리스트로 변환하여 반환
        return calendars.stream()
                .map(Calendar::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CalendarDto updateCalendar(Long id, CalendarDto calendarDto) {
        // 캘린더 조회
        Calendar existingCalendar = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("캘린더를 찾을 수 없습니다."));

        // 기존 캘린더 정보를 수정
        existingCalendar.setCalendarName(calendarDto.getCalendarName());
        existingCalendar.setColor(calendarDto.getColor());

        // 수정된 캘린더를 저장
        Calendar updatedCalendar = calendarRepository.save(existingCalendar);

        // 저장된 캘린더를 DTO로 변환하여 반환
        return updatedCalendar.toDto();
    }

    @Override
    public void deleteCalendar(Long id) {
        // 캘린더 조회
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("캘린더를 찾을 수 없습니다."));

        // 캘린더 삭제
        calendarRepository.delete(calendar);
    }
}
