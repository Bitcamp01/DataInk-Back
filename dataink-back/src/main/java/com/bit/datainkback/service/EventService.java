package com.bit.datainkback.service;

import com.bit.datainkback.dto.EventDto;

import java.util.List;

public interface EventService {

    EventDto createEvent(Long loggedInUserId, EventDto eventDto);

    List<EventDto> getEventsByUserId(Long loggedInUserId);

    List<EventDto> updateEvent(Long id, EventDto eventDto, Long userId);

    List<EventDto> deleteEvent(Long id, Long userId);
}
