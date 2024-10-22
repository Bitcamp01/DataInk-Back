package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public  class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;  // 프로젝트 소유자를 저장하기 위한 UserRepository

    public ProjectDto createProject(ProjectDto projectDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        projectDto.setStartDate(LocalDateTime.now());  // 생성일자 설정

        Project savedProject = projectRepository.save(projectDto.toEntity(user));

        return savedProject.toDto();
    }

    @Override
    public List<ProjectDto> getProjectByUser(Long id) {
        return List.of();
    }
}
