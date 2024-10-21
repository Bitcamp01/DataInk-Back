package com.bit.datainkback.controller;


import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.mongo.FolderDto;
import com.bit.datainkback.entity.CustomUserDetails;
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

import java.util.List;

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
    //프론트 단에서 db에 사용되는 유저 인덱스id를 확인할 수 없음, 따라서 user db의 id 컬럼을 사용하는 userName이용
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
<<<<<<< HEAD

=======
>>>>>>> 6306a9dcfc833636b65773b7e71259006cae75ab
}
