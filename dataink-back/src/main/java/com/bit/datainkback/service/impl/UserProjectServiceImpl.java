package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserProject;
import com.bit.datainkback.entity.UserProjectId;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.UserProjectRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.UserProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserProjectServiceImpl implements UserProjectService {

    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;


    public UserProjectServiceImpl(UserProjectRepository userProjectRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.userProjectRepository = userProjectRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
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

    @Override
    public UserProject updateUserProject(Long projectId, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if (userOptional.isPresent() && projectOptional.isPresent()) {
            UserProjectId userProjectId = new UserProjectId(userId, projectId);
            UserProject userProject=new UserProject();
            userProject.setId(userProjectId);
            userProject.setUser(userOptional.get());
            userProject.setProject(projectOptional.get());
            userProject.setUserWorkcnt(0);
            userProject.setBookmarked(false);
            userProject.setCompletedInspection(0);
            userProject.setPendingInspection(0);
            userProject.setTotalWorkcnt(0);
            return userProjectRepository.save(userProject);
        }
        else {
            throw new RuntimeException("user or project not found");
        }


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
}
