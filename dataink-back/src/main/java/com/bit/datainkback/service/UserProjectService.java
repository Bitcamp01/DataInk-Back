package com.bit.datainkback.service;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.entity.UserProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface UserProjectService {
    List<ProjectDto> getProjectDtosByUserId(Long userId);

    List<UserProjectDto> getUserProjectDtosByUserId(Long userId);

    UserProject updateUserProject(Long projectId, Long userId);
    // 북마크 상태 업데이트 메서드
    UserProjectDto updateBookmarkStatus(Long userId, Long projectId, boolean isBookmarked);

    //프로젝트멤버 모달 메서드
    List<UserDto> getMembersByProjectId(Long projectId);
    List<UserProjectDto> addMembersToProject(Long projectId, List<Long> newMembers);
    void removeMembersFromProject(Long projectId, List<Long> userIds);

    List<String> getJoinedUserIds(Long projectId);
  
  //    Page<ProjectDto> findAll(String searchCondtion, String searchKeyword, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, Long loggedInUserId);

}
