package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.repository.mongo.FolderRepository;
import com.bit.datainkback.repository.mongo.MongoProjectDataRepository;
import com.bit.datainkback.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public  class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MongoProjectDataRepository mongoProjectDataRepository;
    @Autowired
    private FolderRepository mongoProjectFolderRepository;
    @Autowired
    private UserRepository userRepository;  // 프로젝트 소유자를 저장하기 위한 UserRepository

    public ProjectDto createProject(ProjectDto projectDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        projectDto.setStartDate(LocalDateTime.now());  // 생성일자 설정

        Project savedProject = projectRepository.save(projectDto.toEntity(user));

        return savedProject.toDto();
    }

    @Override
    public List<ProjectDto> getProjectByUser(Long id) {
        // 1. 사용자 ID로 프로젝트 필터링
        List<Project> projects = projectRepository.findAll().stream()
                .filter(project -> project.getUser().getUserId().equals(id))
                .toList();

        List<ProjectDto> projectDtos = new ArrayList<>();

        for (Project project : projects) {
            // 2. MongoProjectData 조회
            var mongoProjectData = mongoProjectDataRepository.findByProjectId(project.getProjectId());
            List<String> folderIds = new ArrayList<>();

            // 3. 폴더 ID가 존재할 경우 처리
            if (mongoProjectData.isPresent()) {
                folderIds = mongoProjectData.get().getFolders();
            }

            // 4. 폴더 ID로 MongoDB에서 폴더 조회 (Optional 처리)
            List<Folder> folders = folderIds.stream()
                    .map(folderId -> mongoProjectFolderRepository.findById(folderId).orElse(null)) // 폴더가 없으면 null 반환
                    .filter(Objects::nonNull)  // null 필터링
                    .collect(Collectors.toList());

            // 5. ProjectDto로 변환 및 폴더 설정
            var projectDto = project.toDto();
            projectDto.setFolders(folders);

            // 6. 결과 리스트에 추가
            projectDtos.add(projectDto);
        }

        // 7. 로깅 및 반환
        log.info("Projects: " + projectDtos.toString());
        return projectDtos;
    }


    @Override
    public ProjectDto getProjectById(Long selectedProject) {
        Project project=projectRepository.findById(selectedProject).orElse(null);
        var s=mongoProjectDataRepository.findByProjectId(project.getProjectId());
        List<String> folderIds=new ArrayList<>();
        if (s.isPresent()){
            folderIds=s.get().getFolders();
        }

        List<Folder> folders=new ArrayList<>();
        for (String folder : folderIds) {
            var ss=mongoProjectFolderRepository.findById(folder).get();
            folders.add(ss);
        }
        var a=project.toDto();
        a.setFolders(folders);

        return a;
    }

    @Override
    public MongoProjectData getProjectDataById(Long selectedProject) {
        return mongoProjectDataRepository.findByProjectId(selectedProject).orElse(null);
    }

    @Override
    public void updateProjectData(MongoProjectData projectData) {
        mongoProjectDataRepository.save(projectData);
    }

    @Override
    public void deleteProject(Long i) {
        Project project=projectRepository.findById(i).orElseThrow(() -> new RuntimeException("Project not found"));
        var s=mongoProjectDataRepository.findByProjectId(project.getProjectId());
        if (s.isPresent()){
            mongoProjectDataRepository.delete(s.get());
        }
        projectRepository.delete(project);
    }

    @Override
    public void modifyProjectName(String label, Long selectedProject) {
        Project project=projectRepository.findById(selectedProject).orElseThrow(() -> new RuntimeException("Project not found"));
        project.setName(label);
        projectRepository.save(project);
    }
}
