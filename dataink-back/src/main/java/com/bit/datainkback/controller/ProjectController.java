package com.bit.datainkback.controller;


import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.mongo.FolderDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import com.bit.datainkback.enums.TaskStatus;
import com.bit.datainkback.service.ProjectService;
import com.bit.datainkback.service.mongo.FolderService;
import com.bit.datainkback.service.mongo.MongoProjectDataService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private MongoProjectDataService mongoProjectDataService;

    @Autowired
    private FolderService folderService;

    // 프로젝트 생성 API (MySQL 및 MongoDB에 저장)
    @PostMapping("/create")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto,@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info(projectDto.toString());
        // 프로젝트 생성 (RDBMS 저장)
        ProjectDto savedProject = projectService.createProject(projectDto,customUserDetails.getUser().getUserId());

        // MongoDB에 폴더 및 라벨링 데이터를 저장 (폴더, tasks 포함)
        mongoProjectDataService.createMongoProjectData(savedProject.getProjectId());

        // 로그 확인
        log.info("createProject projectDto.");

        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
    }
    //하위 폴더 생성
    @PostMapping("/createfolder")
    public ResponseEntity<FolderDto> createFolder(@RequestBody Map<String, Object> requestData,@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String selectedFolder = String.valueOf(requestData.get("selectedFolder"));
        Long selectedProject = ((Number) requestData.get("selectedProject")).longValue();
        log.info(selectedFolder);
        log.info(selectedProject.toString());
        FolderDto folderDto = new FolderDto();
        folderDto.setLabel("NewFolder");
        folderDto.setFolder(true);
        folderDto.setChildren(List.of());
        folderDto.setFinished(false);
        folderDto.setItemIds(List.of());
        folderDto.setLastModifiedDate(LocalDateTime.now().toString());
        folderDto.setLastModifiedUserId(customUserDetails.getUser().getId());
        Folder newFolder = folderDto.toEntity();
        newFolder.generateId();  // MongoDB ID 생성
          // 부모 폴더 ID로 조회
        if (selectedFolder.equalsIgnoreCase(selectedProject.toString())){
            // 프로젝트 데이터에 새 폴더 추가
            MongoProjectData projectData = projectService.getProjectDataById(selectedProject);
            folderService.createFolder(newFolder);
            projectData.getFolders().add(newFolder.getId());  // 폴더 ID 추가
            projectService.updateProjectData(projectData);

        }
        else {
            Folder parentFolder = folderService.getFolderById(selectedFolder);
            if (parentFolder != null) {
                parentFolder.getChildren().add(newFolder);  // 자식 폴더 리스트에 추가
                folderService.updateFolder(parentFolder);   // 부모 폴더 업데이트
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 부모 폴더를 찾지 못한 경우
            }
        }

        return ResponseEntity.ok(newFolder.toDto());  // 필요시 적절한 DTO 반환
    }
    @PostMapping("/rename")
    public ResponseEntity<String> renameProject(@RequestBody Map<String, Object> requestData) {
        String label = (String) requestData.get("label");
        String selectedFolder = (String) requestData.get("selectedFolder");
        Long selectedProject = ((Number) requestData.get("selectedProject")).longValue();
        if (selectedFolder.equalsIgnoreCase(selectedProject.toString())){
            return ResponseEntity.ok("ok");
        }
        else {
            folderService.modifyFolder(label,selectedFolder);
            return ResponseEntity.ok("ok");
        }
    }
    //특정 폴더 정보 가져오기, 프로젝트를 최상위 폴더로 다루므로 별도 처리 필요
    @GetMapping("/folder")
    public ResponseEntity<List<Folder>> getFolderData(@RequestParam("selectedFolder") String selectedFolder,
                                                @RequestParam("selectedProject") Long selectedProject
                                                ) {

        if (selectedFolder.equalsIgnoreCase(selectedProject.toString())){
            ProjectDto projectDto=projectService.getProjectById(selectedProject);
            log.info(projectDto.toString());
            return ResponseEntity.ok(projectDto.getFolders());
        }
        else {
            return ResponseEntity.ok(folderService.getFolderById(selectedFolder).getChildren());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<ProjectDto>> getAllProjects(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long id=customUserDetails.getUser().getUserId();
        List<ProjectDto> getProject=projectService.getProjectByUser(id);

        return ResponseEntity.ok(getProject);
    }
    // 프로젝트 데이터 조회 및 트리구조 반환 API
    @GetMapping("/{projectId}/folders")
    public ResponseEntity<List<FolderDto>> getProjectFolders(@PathVariable Long projectId) {
        // 1. 프로젝트 ID로 폴더 ID들 조회
        List<String> folderIds = mongoProjectDataService.getFolderIdsByProjectId(projectId);

        // 2. 해당 폴더 ID에 맞는 폴더 트리 조회
        List<FolderDto> folderTree = folderService.getFolderTreeByIds(folderIds);

        // 3. 폴더 구조를 트리 형태로 반환
        return ResponseEntity.ok(folderTree);
    }

}
