package com.bit.datainkback.repository.custom;

import com.bit.datainkback.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ProjectRepositoryCustom {
    Page<Project> searchAll(String searchCondition, String searchKeyword, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, Long loggedInUserId);
}
