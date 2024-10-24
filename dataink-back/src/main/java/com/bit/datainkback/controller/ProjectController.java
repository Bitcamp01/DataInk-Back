package com.bit.datainkback.controller;


import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.mongo.FolderDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import com.bit.datainkback.enums.TaskStatus;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.service.ProjectService;
import com.bit.datainkback.service.mongo.FieldService;
import com.bit.datainkback.service.mongo.FolderService;
import com.bit.datainkback.service.mongo.MongoProjectDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import java.time.LocalDate;
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
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private FieldService fieldService;
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
        String selectedFolder = requestData.get("selectedFolder").toString();
        Long selectedProject = ((Number) requestData.get("selectedProject")).longValue();
        if (selectedFolder.equalsIgnoreCase(selectedProject.toString())){
            projectService.modifyProjectName(label,selectedProject);
            return ResponseEntity.ok("ok");
        }
        else {
            folderService.modifyFolder(label,selectedFolder);
            return ResponseEntity.ok("ok");
        }
    }
    @PostMapping("/delete")
    public ResponseEntity<String> deleteFolder(@RequestBody Map<String, Object> requestData) {
        List<String> ids = (List<String>) requestData.get("ids");
        Map<String,String> parseIds=new HashMap<>();
        for (String id : ids){
            String[] split = id.split("_");
            parseIds.put(split[0],split[1]);
        }
        for (Map.Entry<String, String> entry : parseIds.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            //프로젝트 삭제
            if (entry.getKey().equalsIgnoreCase(entry.getValue())) {
                projectService.deleteProject(Long.parseLong(entry.getValue()));
            }
            //하위 폴더 삭제
            else {
                folderService.deleteFolder(entry.getKey());
            }
        }
        return ResponseEntity.ok("ok");

    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadfile(@RequestParam("selectedFolder") String selectedFolder,
                                             @RequestParam("selectedProject") Long selectedProject,
                                             @RequestParam("files") MultipartFile[] files) {


        return ResponseEntity.ok("ok");

    }
    @PostMapping("/itemcreate")
    public ResponseEntity<String> itemcreate(@RequestBody Map<String, Object> requestData) {
        log.info(requestData.toString());

        // itemName 추출
        String label = requestData.get("itemName").toString();

        // data 추출
        var s = requestData.get("data");
        String parse = s.toString();
        parse = fixJsonString(parse);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 Map으로 변환
            Map<String, Object> map = objectMapper.readValue(parse, Map.class);
            Field rootField = new Field();
            List<Field> fields = new ArrayList<>();
            // 중첩된 필드 처리
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    // Map 처리
                    Field field = mapJsonToField(entry.getKey(), (Map<String, Object>) entry.getValue());
                    fields.add(field);
                    log.info("Field created: " + field.toString());
                } else {
                    log.info("Non-map value: " + entry.getValue().toString());
                }
            }
            rootField.setSubFields(fields);
            rootField.setFieldName(label);
            fieldService.createField(rootField);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    public static String fixJsonString(String jsonString) {
        // 1. '='을 ':'로 바꾸기
        jsonString = jsonString.replaceAll("=", ":");

        // 2. 필드 이름에 큰따옴표 추가
        jsonString = jsonString.replaceAll("([a-zA-Z0-9가-힣]+)(:)", "\"$1\":");

        // 3. 빈 값에 대한 처리 (빈 값을 빈 문자열로 교체)
        jsonString = jsonString.replaceAll(":,", ":\"\",");  // 빈 값은 ""로 처리
        jsonString = jsonString.replaceAll(":\\}", ":\"\"}");  // 빈 객체는 ""로 처리

        // 4. 잘못된 콤마 제거 및 중첩 객체 처리
        jsonString = jsonString.replaceAll(",\\{", ",{\"");
        jsonString = jsonString.replaceAll("\\},\\{", "},{");

        // 5. 빈 값으로 남는 부분을 빈 문자열로 교체
        jsonString = jsonString.replaceAll(":\\{\\}", ":\"\"");  // 빈 중첩 객체는 ""로 처리

        return jsonString;
    }



    public static Field mapJsonToField(String fieldName, Map<String, Object> jsonField) {
        Field field = new Field();
        field.setFieldName(fieldName);

        // 하위 필드가 있는 경우, subFields에 추가
        List<Field> subFields = new ArrayList<>();

        for (Map.Entry<String, Object> entry : jsonField.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // 하위 필드가 있는 경우 재귀적으로 처리
                Field subField = mapJsonToField(key, (Map<String, Object>) value);
                subFields.add(subField);
            } else {
                // value가 문자열인 경우, 하위 필드가 없는 단순 필드로 처리
                Field subField = new Field();
                subField.setFieldName(key);
                subField.setParentField(false);
                subFields.add(subField);
            }
        }

        field.setSubFields(subFields);
        field.setParentField(!subFields.isEmpty());  // 하위 필드가 있으면 상위 항목으로 설정
        return field;
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


    // 프로젝트의 마감일 가져오기
    @GetMapping("/enddate/{projectId}")
    public ResponseEntity<String> getProjectEndDateById(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("프로젝트를 찾을 수 없습니다."));
        LocalDate endDate = project.getEndDate().toLocalDate().minusDays(1); // 하루 빼기
        return ResponseEntity.ok(endDate.toString());
    }


}
