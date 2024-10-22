package com.bit.datainkback.service;

import com.bit.datainkback.dto.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {
    public ProjectDto createProject(ProjectDto projectDto, Long userId);

    List<ProjectDto> getProjectByUser(Long id);


}
