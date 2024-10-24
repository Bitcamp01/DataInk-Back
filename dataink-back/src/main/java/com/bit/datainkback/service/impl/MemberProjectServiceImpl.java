package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.service.MemberProjectService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberProjectServiceImpl implements MemberProjectService {
    private ProjectRepository projectRepository;

    @Override
    public Page<ProjectDto> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable).map(Project::toDto);
    }
}
