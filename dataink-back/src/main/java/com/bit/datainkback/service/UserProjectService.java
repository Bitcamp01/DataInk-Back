package com.bit.datainkback.service;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.UserProject;

import java.util.List;

public interface UserProjectService {
    List<ProjectDto> getUserProjectsByUserId(Long userId);
}
