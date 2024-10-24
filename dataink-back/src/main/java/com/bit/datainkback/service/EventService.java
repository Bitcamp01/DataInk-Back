package com.bit.datainkback.service;

import com.bit.datainkback.dto.EventDto;

import java.util.List;

public interface EventService {

    EventDto createEvent(Long loggedInUserId, EventDto eventDto);

    List<EventDto> getEventsByUserId(Long loggedInUserId);

    EventDto updateEvent(Long id, EventDto eventDto);

    void deleteEvent(Long id);
}
