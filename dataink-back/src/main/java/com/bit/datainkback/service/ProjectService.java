package com.bit.datainkback.service;

import com.bit.datainkback.dto.ProjectDto;
<<<<<<< HEAD
import com.bit.datainkback.entity.mongo.MongoProjectData;
=======
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
>>>>>>> 4d0bccb7f90060d32a1df2aed5dd63566830f32c

import java.util.List;

public interface ProjectService {
    public ProjectDto createProject(ProjectDto projectDto, Long userId);

    List<ProjectDto> getProjectByUser(Long id);

<<<<<<< HEAD
    ProjectDto getProjectById(Long selectedProject);

    MongoProjectData getProjectDataById(Long selectedProject);

    void updateProjectData(MongoProjectData projectData);

    void deleteProject(Long i);
=======

>>>>>>> 4d0bccb7f90060d32a1df2aed5dd63566830f32c
}
