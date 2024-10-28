package com.bit.datainkback.service;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.entity.UserProject;

import java.util.List;

public interface UserProjectService {
    List<ProjectDto> getProjectDtosByUserId(Long userId);

    List<UserProjectDto> getUserProjectDtosByUserId(Long userId);
    UserProject updateUserProject(Long projectId,Long userId);
    // 북마크 상태 업데이트 메서드
    UserProjectDto updateBookmarkStatus(Long userId, Long projectId, boolean isBookmarked);
}
