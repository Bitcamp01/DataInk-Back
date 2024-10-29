package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.UserProject;
import com.bit.datainkback.entity.UserProjectId;
import com.bit.datainkback.repository.UserProjectRepository;
import com.bit.datainkback.service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public List<ProjectDto> getProjectDtosByUserId(Long userId) {
        // User ID로 UserProject 목록 조회 후 Project만 추출하여 DTO로 변환
        return userProjectRepository.findByUserUserId(userId).stream()
                .map(userProject -> userProject.getProject().toDto())
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProjectDto> getUserProjectDtosByUserId(Long userId) {
        // User ID로 UserProject 목록 조회 후 UserProject를 DTO로 변환
        return userProjectRepository.findByUserUserId(userId).stream()
                .map(UserProject::toDto)
                .collect(Collectors.toList());
    }

    // 북마크 상태 업데이트 메서드
    public UserProjectDto updateBookmarkStatus(Long userId, Long projectId, boolean isBookmarked) {
        // UserProject 엔티티를 가져옴, 없으면 예외 발생
        UserProject userProject = userProjectRepository.findById(new UserProjectId(userId, projectId))
                .orElseThrow(() -> new RuntimeException("UserProject entry not found"));

        // 북마크 상태 업데이트
        userProject.setBookmarked(isBookmarked);

        // 변경된 엔티티를 저장
        userProjectRepository.save(userProject);

        // 업데이트된 UserProject를 DTO로 반환
        return userProject.toDto();
    }

//    @Override
//    public Page<ProjectDto> findAll(String searchCondition, String searchKeyword, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, Long loggedInUserId) {
//        List<Long> projectIds = userProjectRepository.findBy();
//
//        return projectRepository
//                .searchAll(searchCondition, searchKeyword, pageable, startDate, endDate, loggedInUserId)
//                .map(Project::toDto);
//    }
}
