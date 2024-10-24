package com.bit.datainkback.service;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface ProjectService {
    public ProjectDto createProject(ProjectDto projectDto, Long userId);

    List<ProjectDto> getProjectByUser(Long id);


    ProjectDto getProjectById(Long selectedProject);

    MongoProjectData getProjectDataById(Long selectedProject);

    void updateProjectData(MongoProjectData projectData);

    void deleteProject(Long i);

    void modifyProjectName(String label, Long selectedProject);
}
