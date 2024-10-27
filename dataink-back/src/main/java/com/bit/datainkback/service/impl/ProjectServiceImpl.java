package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.mongo.FolderDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.repository.mongo.FieldRepository;
import com.bit.datainkback.repository.mongo.FolderRepository;
import com.bit.datainkback.repository.mongo.MongoProjectDataRepository;
import com.bit.datainkback.service.ProjectService;
import com.bit.datainkback.service.mongo.FolderService;
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
    //급해서 추가함, 추후 변경 필요
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    private MongoProjectDataRepository mongoProjectDataRepository;
    @Autowired
    private FolderRepository mongoProjectFolderRepository;
    @Autowired
    private UserRepository userRepository;  // 프로젝트 소유자를 저장하기 위한 UserRepository
    @Autowired
    private FieldRepository fieldRepository;

    public ProjectDto createProject(ProjectDto projectDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        projectDto.setStartDate(LocalDateTime.now());  // 생성일자 설정

        Project savedProject = projectRepository.save(projectDto.toEntity(user));

        return savedProject.toDto();
    }
    public Folder getFolderTree(String folderId) {
        Folder folder1 = folderRepository.findById(folderId).orElse(null);
        //현재 db 에 문제 있음, 추후 db 전부 밀고 다시 시작하면 해결
//                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));
        if (Objects.isNull(folder1)) {
            return null;
        }
        Folder folder= new Folder();
        folder.setId(folder1.getId());
        folder.setLabel(folder1.getLabel());
        folder.setLastModifiedDate(folder1.getLastModifiedDate());
        folder.setChildren(new ArrayList<>());

        if (folder1.getChildren() != null && !folder1.getChildren().isEmpty()) {
            for (Folder childFolder : folder1.getChildren()) {
                Folder childFolderDto = getFolderTree(childFolder.getId());
                if (Objects.nonNull(childFolderDto)) {
                    folder.getChildren().add(childFolderDto);
                }
            }
        }

        return folder;
    }
    @Override
    public List<ProjectDto> getProjectByUser(Long id) {
        List<Project> projects = projectRepository.findAll().stream()
                .filter(project -> project.getUser().getUserId().equals(id))
                .toList();

        List<ProjectDto> projectDtos = new ArrayList<>();

        for (Project project : projects) {

            var mongoProjectData = mongoProjectDataRepository.findByProjectId(project.getProjectId());
            List<String> folderIds = new ArrayList<>();

            if (mongoProjectData.isPresent()) {
                folderIds = mongoProjectData.get().getFolders();
            }


            List<Folder> folders = new ArrayList<>();
            for (String folderId : folderIds) {
                Folder folder=getFolderTree(folderId);
                if (folder != null) {
                    log.info("folder: {}", folder);
                    folders.add(folder);
                }
            }


            var projectDto = project.toDto();
            projectDto.setFolders(folders);


            projectDtos.add(projectDto);
        }

        return projectDtos;
    }


    @Override
    public ProjectDto getProjectById(Long selectedProject) {
        //프로젝트 아이디로부터 RDB 프로젝트 엔티티 얻음
        Project project=projectRepository.findById(selectedProject).orElse(null);
        // 얻은 프로젝트 아이디로 부터 몽고 프로젝트 엔티티 얻음(바로 하위 폴더 정보 있음)
        MongoProjectData mongoProjectData=mongoProjectDataRepository.findByProjectId(project.getProjectId()).orElseThrow(()-> new RuntimeException("Project not found"));
        //하위 폴더 아이디 모음
        List<String> folderIds=mongoProjectData.getFolders();

        List<Folder> folders=new ArrayList<>();
        for (String folder : folderIds) {
            //폴더 아이디로 폴더 가져옴
            Folder mongoFolder=mongoProjectFolderRepository.findById(folder).orElseThrow(()-> new RuntimeException("Folder not found"));
            //project에 넣어줄 folder에 추가
            folders.add(mongoFolder);
        }
        //가져온 rdb프로젝트 엔티티의 folder에 가져온 폴더 정보 넣어줌
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
    public Project modifyProjectName(String label, Long selectedProject) {
        Project project=projectRepository.findById(selectedProject).orElseThrow(() -> new RuntimeException("Project not found"));
        project.setName(label);
        return projectRepository.save(project);
    }
}
