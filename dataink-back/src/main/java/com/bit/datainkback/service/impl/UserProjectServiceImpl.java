package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.entity.UserProject;
import com.bit.datainkback.repository.UserProjectRepository;
import com.bit.datainkback.service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProjectServiceImpl implements UserProjectService {

    @Autowired
    private final UserProjectRepository userProjectRepository;

    public UserProjectServiceImpl(UserProjectRepository userProjectRepository) {
        this.userProjectRepository = userProjectRepository;
    }

    @Override
    public List<ProjectDto> getUserProjectsByUserId(Long userId) {
        // User ID로 UserProject 목록 조회
        List<UserProject> userProjects = userProjectRepository.findByUserUserId(userId);

        // UserProject 목록에서 Project 엔티티 추출 후 DTO로 변환
        return userProjects.stream()
                .map(userProject -> userProject.getProject().toDto())
                .collect(Collectors.toList());
    }
}
