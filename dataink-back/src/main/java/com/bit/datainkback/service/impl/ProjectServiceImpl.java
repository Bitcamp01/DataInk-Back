package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.mongo.Folder;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {
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
        List<Project> projects=projectRepository.findAll();
        projects=projects.stream().filter(project -> project.getUser().getUserId().equals(id)).toList();
        List<ProjectDto> projectDtos=new ArrayList<>();
        for (Project project : projects) {

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
            projectDtos.add(a);
        }
        log.info(projectDtos.toString());
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
}
