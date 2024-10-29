package com.bit.datainkback.service;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import org.springframework.boot.configurationprocessor.json.JSONObject;


import java.util.HashMap;
import java.util.List;

public interface ProjectService {
    public ProjectDto createProject(ProjectDto projectDto, Long userId);

    List<ProjectDto> getProjectByUser(Long id);


    ProjectDto getProjectById(Long selectedProject);

    MongoProjectData getProjectDataById(Long selectedProject);

    void updateProjectData(MongoProjectData projectData);

    void deleteProject(Long i);

    Project modifyProjectName(String label, Long selectedProject);

    List<JSONObject> getJson(HashMap<String, String> hasConversion);

    List<JSONObject> getJsonProjectStructure(HashMap<String, String> hasConversion);

    ProjectDto getProjectWithFolder(Long projectId);

    double getProjectProgress(List<Folder> folders);
}
