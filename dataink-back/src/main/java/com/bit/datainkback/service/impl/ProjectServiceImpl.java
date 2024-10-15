package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.ProjectService;
import com.bit.datainkback.service.mongo.MongoProjectDataService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;  // 프로젝트 소유자를 저장하기 위한 UserRepository

    @Autowired
    private MongoProjectDataService mongoProjectDataService;  // MongoDB 관련 서비스 주입

    public ProjectDto createProject(ProjectDto projectDto) {
        User user = userRepository.findById(projectDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        projectDto.setStartDate(LocalDateTime.now());  // 생성일자 설정

        Project savedProject = projectRepository.save(projectDto.toEntity(user));

        // MongoDB에 폴더 구조 저장 후, 해당 ID를 반환받아 RDB에 저장
        String mongoDataId = mongoProjectDataService.createMongoProjectData(savedProject.getProjectId(), projectDto.getFolders());
        savedProject.setMongoDataId(mongoDataId);  // RDB에 mongoDataId 저장

        return projectRepository.save(savedProject).toDto();  // 수정된 프로젝트 정보 반환
    }
}
