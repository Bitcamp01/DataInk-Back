package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserProject;
import com.bit.datainkback.entity.UserProjectId;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.UserProjectRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.UserProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProjectServiceImpl implements UserProjectService {
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

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

    @Override
    public List<UserDto> getMembersByProjectId(Long projectId) {
        List<Long> projectMemberIds =  userProjectRepository.findByProjectProjectId(projectId)
                .stream()
                .map(UserProject -> UserProject.getId().getUserId())
                .collect(Collectors.toList());

        List<UserDto> projectMembers = userRepository.findAllById(projectMemberIds)
                .stream()
                .peek(user -> user.setPassword("")) // 각 User 객체의 password를 빈 문자열로 초기화
                .map(User::toDto)
                .collect(Collectors.toList());

        return projectMembers;
    }

    @Override
    public List<UserProjectDto> addMembersToProject(Long projectId, List<Long> newMemberIds) {
        try {
            Project project = projectRepository.findById(projectId).orElseThrow(() -> new Exception("존재하지않는 프로젝트입니다."));
            List<UserProject> userProjects = userProjectRepository.findByProjectProjectId(projectId);
            userProjectRepository.deleteAll(userProjects);
            List<UserProject> newUserProjects = new ArrayList<>();
            for (Long newMemberId : newMemberIds) {
                User user = userRepository.findById(newMemberId).orElseThrow(() -> new Exception("존재하지않는 유저입니다."));
                UserProjectId userProjectId = new UserProjectId();
                userProjectId.setUserId(newMemberId);
                userProjectId.setProjectId(projectId);

                UserProject userProject = UserProject.builder()
                        .id(userProjectId)
                        .project(project)
                        .user(user)
                        .build();
                newUserProjects.add(userProject);
            }
            userProjectRepository.saveAll(newUserProjects);
            List<UserProjectDto> savedUserProjectDtos = newUserProjects.stream()
                    .map(UserProject::toDto)
                    .collect(Collectors.toList());
            return savedUserProjectDtos;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public void removeMembersFromProject(Long projectId, List<Long> userIds) {
        for (Long userId : userIds) {
            UserProjectId id = new UserProjectId(userId, projectId);
            userProjectRepository.deleteById(id);
        }
    }

    @Override
    public List<String> getJoinedUserIds(Long projectId) {
        List<Long> joinedUserIds = userProjectRepository.findByProjectProjectId(projectId)
                .stream()
                .map(userProject -> userProject.getId().getUserId())
                .collect(Collectors.toList());

        List<String> joinedUserNames = userRepository.findAllById(joinedUserIds)
                .stream()
                .map(user -> user.getName())
                .collect(Collectors.toList());
        return joinedUserNames;
    }

    @Override
    public Page<ProjectDto> findAll(String searchCondition, String searchKeyword, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, Long loggedInUserId) {
        List<Long> projectIds = userProjectRepository.findByUserUserId(loggedInUserId).stream()
                .map(userProject -> userProject.getProject().getProjectId())
                .collect(Collectors.toList());

        return projectRepository
                .searchAll(searchCondition, searchKeyword, pageable, startDate, endDate, projectIds)
                .map(Project::toDto);
    }

}
