package com.bit.datainkback.service;

import com.bit.datainkback.dto.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberProjectService {
    Page<ProjectDto> getAllProjects(Pageable pageable);
}
