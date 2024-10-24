package com.bit.datainkback.repository;

import com.bit.datainkback.entity.Calendar;
import com.bit.datainkback.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUser_UserId(Long userId);
}
