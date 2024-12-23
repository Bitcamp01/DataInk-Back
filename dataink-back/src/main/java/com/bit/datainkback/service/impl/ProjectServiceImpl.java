package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.enums.TaskStatus;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.repository.mongo.FieldRepository;
import com.bit.datainkback.repository.mongo.FolderRepository;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import com.bit.datainkback.repository.mongo.MongoProjectDataRepository;
import com.bit.datainkback.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j

public class ProjectServiceImpl implements ProjectService {

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
    @Autowired
    private MongoLabelTaskRepository labelTaskRepository;
    public ProjectDto createProject(ProjectDto projectDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        projectDto.setStartDate(LocalDateTime.now());  // 생성일자 설정

        Project savedProject = projectRepository.save(projectDto.toEntity(user));

        return savedProject.toDto();
    }
    public ProjectDto getProjectWithFolder(Long projectId){

        MongoProjectData project=mongoProjectDataRepository.findByProjectId(projectId).orElseThrow(()-> new RuntimeException("not found project"));
        List<String> projectUnderFolders=project.getFolders();
        List<Folder> folders=new ArrayList<>();
        for (String folderId: projectUnderFolders){
            Folder folder=getFolderTree(folderId);
            folders.add(folder);
        }
        ProjectDto returnProject=projectRepository.findById(projectId).orElseThrow(()-> new RuntimeException("not found project")).toDto();
        returnProject.setFolders(folders);
        return returnProject;
    }

    @Override
    public double getProjectProgress(List<String> folders) {
        long allTask=0;
        long finishedTask=0;
        Queue<String> searchFolderIds=new ArrayDeque<>();
        for (String folder : folders) {
            searchFolderIds.add(folder);
        }
        while (!searchFolderIds.isEmpty()) {
            String getFolder = searchFolderIds.poll();
            Folder folder=folderRepository.findById(getFolder).orElseThrow(()-> new RuntimeException("not found folder"));
            if (folder.isFolder()){
                for (Folder childFolder: folder.getChildren()){
                    searchFolderIds.add(childFolder.getId());
                }
            }
            else{
                Tasks tasks=labelTaskRepository.findById(folder.getId()).orElseThrow(()-> new RuntimeException("not found task"));
                allTask++;
                try {
                    if (tasks.getStatus() != null && TaskStatus.APPROVED == TaskStatus.valueOf(tasks.getStatus().toUpperCase())) {
                        finishedTask++;
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid task status: " + tasks.getStatus());
                }
            }
        }
        log.info("all task {}",allTask);
        log.info("finished task {}",finishedTask);
        if (allTask ==0){
            return 0;
        }
        return (double)finishedTask/allTask;
    }

    public Folder getFolderTree(String folderId) {
        Folder folder1 = folderRepository.findById(folderId).orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));
        if (Objects.isNull(folder1)) {
            return null;
        }
        Folder folder= new Folder();
        folder.setId(folder1.getId());
        folder.setLabel(folder1.getLabel());
        folder.setLastModifiedDate(folder1.getLastModifiedDate());
        folder.setChildren(new ArrayList<>());
        folder.setFolder(folder1.isFolder());

        if (folder1.getChildren() != null && !folder1.getChildren().isEmpty()) {
            for (Folder childFolder : folder1.getChildren()) {
                if(childFolder.isFolder()){
                    Folder childFolderDto = getFolderTree(childFolder.getId());
                    if (Objects.nonNull(childFolderDto)) {
                        folder.getChildren().add(childFolderDto);
                    }
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

    @Override
    public List<Map<String, String>> getJson(HashMap<String, String> hasConversion) {
        List<Map<String, Object>> jsonList = new ArrayList<>();
        log.info("hasConversion {}", hasConversion);
        for (String folderId : hasConversion.keySet()) {
            Tasks tasks = labelTaskRepository.findById(folderId).orElseThrow(() -> new RuntimeException("not found task"));
            log.info("task {}", tasks);
            if (tasks.getStatus() != null && TaskStatus.APPROVED == TaskStatus.valueOf(tasks.getStatus().toUpperCase())) {
                Map<String, Object> folderMap = new HashMap<>();
                folderMap.put("taskName", tasks.getTaskName());
                folderMap.put("field", tasks.getFieldValue());
                log.info("json map {}",folderMap);
                jsonList.add(folderMap);
            }
        }
        log.info("jsonList {}",transformData(jsonList));
        return transformData(jsonList);
    }
    public static List<Map<String, String>> transformData(List<Map<String, Object>> data) {
        List<Map<String, String>> transformed = new ArrayList<>();

        for (Map<String, Object> item : data) {
            String taskName = (String) item.get("taskName");
            Map<String, Map<String, Object>> fields = (Map<String, Map<String, Object>>) item.get("field");

            for (Map.Entry<String, Map<String, Object>> fieldEntry : fields.entrySet()) {
                Map<String, Object> fieldData = fieldEntry.getValue();
                Map<String, String> transformedEntry = new HashMap<>();

                transformedEntry.put("taskName", taskName);
                transformedEntry.put("content", (String) fieldData.get("content"));

                // hierarchy keys를 동적으로 level로 변환
                int level = 1;
                for (Map.Entry<String, Object> fieldDetail : fieldData.entrySet()) {
                    String key = fieldDetail.getKey();
                    if (key.startsWith("hierarchy")) {
                        transformedEntry.put("level_" + level, (String) fieldDetail.getValue());
                        level++;
                    }
                }

                transformed.add(transformedEntry);
            }
        }

        return transformed;
    }
    // 폴더 트리를 순회하면서 hasConversion 키에 포함되지 않는 파일을 제거하는 메서드
    private Folder filterFolderTree(Folder folder, Set<String> keys) {
        if (!folder.isFolder() && !keys.contains(folder.getId())) {
            return null;
        }

        List<Folder> filteredChildren = new ArrayList<>();
        for (Folder child : folder.getChildren()) {
            Folder filteredChild = filterFolderTree(child, keys);
            if (filteredChild != null) {
                filteredChildren.add(filteredChild);
            }
        }
        folder.setChildren(filteredChildren); // 필터링된 자식들로 갱신
        return folder;
    }
    public Folder getFolderTreeAll(String folderId) {
        Folder folder1 = folderRepository.findById(folderId).orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));
        if (Objects.isNull(folder1)) {
            return null;
        }
        Folder folder= new Folder();
        folder.setId(folder1.getId());
        folder.setLabel(folder1.getLabel());
        folder.setLastModifiedDate(folder1.getLastModifiedDate());
        folder.setChildren(new ArrayList<>());
        folder.setFolder(folder1.isFolder());

        if (folder1.getChildren() != null && !folder1.getChildren().isEmpty()) {
            for (Folder childFolder : folder1.getChildren()) {
                    Folder childFolderDto = getFolderTreeAll(childFolder.getId());
                    if (Objects.nonNull(childFolderDto)) {
                        folder.getChildren().add(childFolderDto);
                    }
            }
        }
        return folder;
    }
    @Override
    public List<JSONObject> getJsonProjectStructure(HashMap<String, String> hasConversion) {
        Map<String, List<Folder>> projectFolders = new HashMap<>();
        //프로젝트 탐색 여부를 사용하기 위한 부분
        HashMap<String, String> projectIds = new HashMap<>();
        for (String projectId : hasConversion.values()) {
            if (!projectIds.containsKey(projectId)) {
                projectIds.put(projectId, projectId);
                Set<String> keys = hasConversion.keySet();
                MongoProjectData project = mongoProjectDataRepository.findByProjectId(Long.parseLong(projectId))
                        .orElseThrow(() -> new RuntimeException("Project not found"));

                List<Folder> folders = new ArrayList<>();
                for (String folderId : project.getFolders()) {
                    Folder folderTree = getFolderTreeAll(folderId);
                    // 지정된 키에 포함되지 않는 파일을 제거한 폴더 트리를 가져옵니다.
                    Folder filteredFolderTree = filterFolderTree(folderTree, keys);
                    if (filteredFolderTree != null) {
                        folders.add(filteredFolderTree);
                    }
                }
                projectFolders.put(projectId, folders);
            }
        }

        return convertProjectsToJson(projectFolders);
    }
    // 필터링된 프로젝트 폴더를 JSON으로 변환
    private List<JSONObject> convertProjectsToJson(Map<String, List<Folder>> projectFolders) {
        List<JSONObject> projectJsonList = new ArrayList<>();
        for (Map.Entry<String, List<Folder>> entry : projectFolders.entrySet()) {
            String projectId = entry.getKey();
            List<Folder> folders = entry.getValue();
            Project project=projectRepository.findById(Long.parseLong(projectId)).orElseThrow(()->new RuntimeException("Project not found"));
            JSONObject projectJson = new JSONObject();
            try {
                projectJson.put("projectName", project.getName());
                projectJson.put("folders", foldersToJsonArray(folders));
                projectJsonList.add(projectJson);
            }
            catch (Exception e){

            }

        }
        return projectJsonList;
    }

    // 폴더 목록을 JSON 배열로 변환하는 메서드
    private List<JSONObject> foldersToJsonArray(List<Folder> folders) {
        List<JSONObject> folderJsonList = new ArrayList<>();
        for (Folder folder : folders) {
            if (!folder.isFolder()) {
                JSONObject folderJson = new JSONObject();
                Tasks tasks=labelTaskRepository.findById(folder.getId()).orElseThrow(()->new RuntimeException("Task not found"));
                try {
                    folderJson.put("taskName", tasks.getTaskName());
                    folderJson.put("fields", tasks.getFieldValue());

                    if (folder.getChildren() != null && !folder.getChildren().isEmpty()) {
                        folderJson.put("children", foldersToJsonArray(folder.getChildren()));
                    }
                    folderJsonList.add(folderJson);
                }
                catch (Exception e){

                }
            }
        }
        return folderJsonList;
    }


}
