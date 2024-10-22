package com.bit.datainkback.service;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.mongo.MongoProjectData;

import java.util.List;

public interface ProjectService {
    public ProjectDto createProject(ProjectDto projectDto, Long userId);

    List<ProjectDto> getProjectByUser(Long id);

    ProjectDto getProjectById(Long selectedProject);

    MongoProjectData getProjectDataById(Long selectedProject);

    void updateProjectData(MongoProjectData projectData);
}
