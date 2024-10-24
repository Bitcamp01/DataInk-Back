package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.EventDto;
import com.bit.datainkback.entity.Calendar;
import com.bit.datainkback.entity.Event;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.CalendarRepository;
import com.bit.datainkback.repository.EventRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;


    @Override
    public EventDto createEvent(Long loggedInUserId, EventDto eventDto) {
        // 사용자 조회
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Calendar calendar = calendarRepository.findById(eventDto.getCalendarId()).orElseThrow(() -> new IllegalArgumentException("Invalid Calendar ID"));

        Event event = eventDto.toEntity(user, calendar);
        Event savedEvent = eventRepository.save(event);
        return savedEvent.toDto();
    }

    @Override
    public List<EventDto> getEventsByUserId(Long userId) {
        List<Event> events = eventRepository.findByUser_UserId(userId);
        return events.stream()
                .map(Event::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto updateEvent(Long eventId, EventDto eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Event ID"));

        event.setTitle(eventDto.getTitle());
        event.setStartDate(eventDto.getStartDate());
        event.setEndDate(eventDto.getEndDate());
        event.setMemo(eventDto.getMemo());

        Event updatedEvent = eventRepository.save(event);

        return updatedEvent.toDto();
    }

    @Override
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }
}
