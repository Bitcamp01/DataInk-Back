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
import com.bit.datainkback.service.FileService;
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
import java.util.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @Autowired
    private FileService fileService;
    // 프로젝트 생성 API (MySQL 및 MongoDB에 저장)
    @PostMapping("/create")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto,@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 프로젝트 생성 (RDBMS 저장)
        ProjectDto savedProject = projectService.createProject(projectDto,customUserDetails.getUser().getUserId());

        // MongoDB에 폴더 및 라벨링 데이터를 저장 (폴더, tasks 포함)
        mongoProjectDataService.createMongoProjectData(savedProject.getProjectId());

        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
    }
    //하위 폴더 생성
    @PostMapping("/createfolder")
    public ResponseEntity<FolderDto> createFolder(@RequestBody Map<String, Object> requestData,@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String selectedFolder = String.valueOf(requestData.get("selectedFolder"));
        Long selectedProject = ((Number) requestData.get("selectedProject")).longValue();
        FolderDto folderDto = new FolderDto();
        folderDto.setLabel("NewFolder");
        folderDto.setFolder(true);
        folderDto.setChildren(List.of());
        folderDto.setFinished(false);
        folderDto.setItemIds(List.of());
        folderDto.setLastModifiedDate(LocalDateTime.now().toString());
        folderDto.setLastModifiedUserId(customUserDetails.getUser().getId());
        Folder newFolder = folderDto.toEntity();
        folderService.createFolder(newFolder);
          // 부모 폴더 ID로 조회
        if (selectedFolder.equals(selectedProject.toString())){
            // 프로젝트 데이터에 새 폴더 추가
            MongoProjectData projectData = projectService.getProjectDataById(selectedProject);
            folderService.createFolder(newFolder);
            projectData.getFolders().add(newFolder.getId());  // 폴더 ID 추가
            projectService.updateProjectData(projectData);

        }
        else {

            Folder parentFolder = folderService.getFolderById(selectedFolder);
            log.info("create folder's parent: {}",parentFolder.toString());
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
    public ResponseEntity<Folder> renameProject(@RequestBody Map<String, Object> requestData,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String label = (String) requestData.get("label");
        String selectedFolder = requestData.get("selectedFolder").toString();
        Long selectedProject = ((Number) requestData.get("selectedProject")).longValue();
        log.info("selectedFolder:{} selectedProject:{}", selectedFolder, selectedProject);
        if (selectedFolder.equals(selectedProject.toString())){
            Project project=projectService.modifyProjectName(label,selectedProject);
            Folder folder=new Folder();
            folder.setId(project.getProjectId().toString());
            folder.setChildren(project.toDto().getFolders());
            folder.setLastModifiedDate(LocalDateTime.now().toString());
            folder.setLastModifiedUserId(customUserDetails.getUser().getId());
            folder.setLabel(project.getName());
            folder.setItemIds(List.of());
            folder.setFolder(true);
            folder.setFinished(false);
            return ResponseEntity.ok(folder);
        }
        else {
            Folder folder=folderService.modifyFolderName(label,selectedFolder,customUserDetails.getUser().getUserId());
            return ResponseEntity.ok(folder);
        }
    }
    @PostMapping("/delete")
    public ResponseEntity<String> deleteFolder(@RequestBody Map<String, Object> requestData) {
        List<String> ids = (List<String>) requestData.get("ids");
        Map<String,String> parseIds=new HashMap<>();
        log.info(ids.toString());
        for (String id : ids){
            String[] split = id.split("_");
            parseIds.put(split[0],split[1]);
        }
        for (Map.Entry<String, String> entry : parseIds.entrySet()) {
            //프로젝트 삭제
            if (entry.getKey().equals(entry.getValue())) {
//                projectService.deleteProject(Long.parseLong(entry.getValue()));
                //몽고 db데이터는 방치
                projectService.deleteProject(Long.parseLong(entry.getKey()));
            }
            //하위 폴더 삭제
            else {
                folderService.deleteFolder(entry.getKey());
            }
        }
        return ResponseEntity.ok("ok");

    }
    @PostMapping("/conversion")
    public ResponseEntity<String> itemConversion(@RequestBody List<String> conversionList) {

        //재귀적으로 변환될 아이디를 찾기
        Queue<String> conversionListIds = new ArrayDeque<>();
        HashMap<String,Boolean> hasConversion = new HashMap<>();
        for (String s:conversionList){
            String[] split = s.split("_");
            //프로젝트 전체에 대해 동작
            if (split[0].equals(split[1])){
                List<Folder> folders=projectService.getProjectById(Long.parseLong(split[1])).getFolders();
                for (Folder folder:folders){
                    if (folder.isFolder()){
                        conversionListIds.add(folder.getId());
                    }
                    else{
                        if (!hasConversion.containsKey(folder.getId())){
                            hasConversion.put(folder.getId(),true);
                        }
                    }
                }
            }
            else {
                conversionListIds.add(split[0]);
            }
        }
        while (!conversionListIds.isEmpty()){
            Folder folder=folderService.getFolderById(conversionListIds.poll());
            if (folder.isFolder() && folder.getChildren().size()>0){
                for (Folder childFolder:folder.getChildren()){
                    conversionListIds.add(childFolder.getId());
                }
            }
            else{
                if (!folder.isFolder()){
                    if (!hasConversion.containsKey(folder.getId())){
                        hasConversion.put(folder.getId(),true);
                    }
                }
            }
        }
        return ResponseEntity.ok("ok");

    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadfile(@RequestParam("selectedFolder") String selectedFolder,
                                             @RequestParam("selectedProject") Long selectedProject,
                                             @RequestParam("files") MultipartFile[] files,
                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        for (MultipartFile file : files) {
            // 파일 처리 로직 (예: S3에 업로드, DB에 메타데이터 저장 등)
            String fileName = fileService.uploadFile(file, "/pdf_file");
            //파일 업로드에 대한 폴더 생성
            Folder newFolder=new Folder();
            newFolder.setFolder(false);
            newFolder.setLabel(fileName);
            newFolder.setChildren(List.of());
            newFolder.setFinished(false);
            newFolder.setItemIds(List.of());
            newFolder.setLastModifiedDate(LocalDateTime.now().toString());
            newFolder.setLastModifiedUserId(customUserDetails.getUser().getId());
            folderService.createFolder(newFolder);
            if (selectedFolder.equalsIgnoreCase(selectedProject.toString())){
                mongoProjectDataService.addFolderToProject(selectedProject,newFolder);
            }
            else{
                Folder parentFolder = folderService.getFolderById(selectedFolder);
                if (parentFolder != null) {
                    parentFolder.getChildren().add(newFolder);  // 자식 폴더 리스트에 추가
                    folderService.updateFolder(parentFolder);   // 부모 폴더 업데이트
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 부모 폴더를 찾지 못한 경우
                }
            }
        }

        return ResponseEntity.ok("Files uploaded successfully.");

    }
    @PostMapping("/item_select")
    public ResponseEntity<String> folderItemSelect(@RequestBody Map<String, Object> requestData) {
        log.info(requestData.toString());
        String selectedItemId = requestData.get("selectedItemId").toString();
        List<String> selectedFolder = (List<String>) requestData.get("selectedFolder");
        List<String[]> splits=new ArrayList<>();
        Stack<String> folderIdStack=new Stack<>();
        for (String s:selectedFolder){
            splits.add(s.split("_"));
        }
        for (String[] split : splits){
            if (split.length >=2){
                //프로젝트에 항목을 거는 행위
                if (split[0].equals(split[1])){
                    MongoProjectData projectData = projectService.getProjectDataById(Long.parseLong(split[1]));
                    List<String> folderIds=projectData.getFolders();
                    for (String folderId : folderIds){
                        folderService.updateFolderFields(folderId,List.of(selectedItemId));
                        Folder folder = folderService.getFolderById(folderId);
                        if (folder.isFolder() && folder.getChildren().size()>0){
                            for (Folder childFolder:folder.getChildren()){
                                log.info("stack");
                                folderIdStack.push(childFolder.getId());
                            }
                        }
                    }
                    while (!folderIdStack.isEmpty()) {
                        String currentFolderId = folderIdStack.pop();
                        folderService.updateFolderFields(currentFolderId, List.of(selectedItemId));

                        Folder currentFolder = folderService.getFolderById(currentFolderId);

                        // 자식 폴더가 있으면 스택에 추가
                        if (currentFolder.isFolder() && currentFolder.getChildren().size() > 0) {
                            for (Folder childFolder : currentFolder.getChildren()) {
                                folderIdStack.push(childFolder.getId());
                            }
                        }
                    }
                }
                //하위 폴더에 항목을 거는 행위
                else{
                    folderIdStack.push(split[0]);
                    while (!folderIdStack.isEmpty()) {
                        String currentFolderId = folderIdStack.pop();
                        folderService.updateFolderFields(currentFolderId, List.of(selectedItemId));

                        Folder currentFolder = folderService.getFolderById(currentFolderId);

                        // 자식 폴더가 있으면 스택에 추가
                        if (currentFolder.isFolder() && currentFolder.getChildren().size() > 0) {
                            for (Folder childFolder : currentFolder.getChildren()) {
                                folderIdStack.push(childFolder.getId());
                            }
                        }
                    }
                }
            }
        }
        return ResponseEntity.ok("ok");

    }
    @PostMapping("/itemcreate")
    public ResponseEntity<String> itemcreate(@RequestBody Map<String, Object> requestData,
                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {


        String label = requestData.get("itemName").toString();
        Map<String, Object> map = (Map<String, Object>) requestData.get("data");

        try {
            // JSON 문자열을 Map으로 변환
            Field rootField = new Field();
            List<Field> fields = new ArrayList<>();
            // 중첩된 필드 처리
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    // Map 처리
                    Field field = mapJsonToField(entry.getKey(), (Map<String, Object>) entry.getValue());
                    field.setParentField(false);
                    fields.add(field);
                } else {
                    log.info("Non-map value: " + entry.getValue().toString());
                }
            }
            rootField.setSubFields(fields);
            rootField.setFieldName(label);
            rootField.setParentField(true);
            rootField.setUserId(customUserDetails.getUser().getUserId());
            fieldService.createField(rootField);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @PostMapping("/itemupdate")
    public ResponseEntity<String> itemupdate(@RequestBody Map<String, Object> requestData,
                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        // itemName 추출
        String label = requestData.get("itemName").toString();
        String itemId = requestData.get("itemId").toString();
        Map<String, Object> map = (Map<String, Object>) requestData.get("data");
        try {
            // JSON 문자열을 Map으로 변환
            Field rootField = fieldService.getFieldById(itemId);
            List<Field> fields = new ArrayList<>();
            // 중첩된 필드 처리
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    // Map 처리
                    Field field = mapJsonToField(entry.getKey(), (Map<String, Object>) entry.getValue());
                    field.setParentField(false);
                    fields.add(field);
                } else {
                    log.info("Non-map value: " + entry.getValue().toString());
                }
            }
            rootField.setSubFields(fields);
            rootField.setFieldName(label);
            rootField.setParentField(true);
            rootField.setUserId(customUserDetails.getUser().getUserId());
            fieldService.updateField(rootField);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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
    @PostMapping("/cut_paste")
    public ResponseEntity<String> cutPaste(@RequestParam("selectedFolder") String selectedFolder,
                                           @RequestParam("selectedProject") Long selectedProject,
                                           @RequestBody List<String> requestData) {

        //프로젝트 바로 아래에 붙여넣기
        if (selectedFolder.equals(selectedProject.toString())) {

        }
        else {
            for (String s:requestData){
                String[] split=s.split("_");
                if (split.length >=2){
//                    if (split[0].equalsIgnoreCase(split[1])){} 프로젝트는 복사,자르기 막음
                    Folder folder=folderService.getFolderById(split[0]);
                    folder.setId(null);
                    Folder folder1=folderService.createFolder(folder);
                    Folder savedFolder=folderService.getFolderById(selectedFolder);
                    savedFolder.getChildren().add(folder1);
                    folderService.updateFolder(savedFolder);
                }
            }
        }
        return ResponseEntity.ok("ok");

    }
    //특정 폴더 정보 가져오기, 프로젝트를 최상위 폴더로 다루므로 별도 처리 필요
    @GetMapping("/folder")
    public ResponseEntity<List<Folder>> getFolderData(@RequestParam("selectedFolder") String selectedFolder,
                                                @RequestParam("selectedProject") Long selectedProject
                                                ) {
        if (selectedFolder.equals(selectedProject.toString())){
            ProjectDto projectDto=projectService.getProjectById(selectedProject);
            return ResponseEntity.ok(projectDto.getFolders());
        }
        else {
            log.info("특정 폴더 정보 {}",folderService.getFolderById(selectedFolder).toString());
            List<Folder> folders=new ArrayList<>();
            for (Folder folder:folderService.getFolderById(selectedFolder).getChildren()){
                folders.add(folderService.getFolderById(folder.getId()));

            }
            return ResponseEntity.ok(folders);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<ProjectDto>> getAllProjects(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long id=customUserDetails.getUser().getUserId();
        List<ProjectDto> getProject=projectService.getProjectByUser(id);

        return ResponseEntity.ok(getProject);
    }
    @GetMapping("/items")
    public ResponseEntity<List<Field>> getItems(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long id=customUserDetails.getUser().getUserId();
        List<Field> fields=fieldService.getFieldByUserId(id);

        return ResponseEntity.ok(fields);
    }
    @GetMapping("/item_structure/{itemId}")
    public ResponseEntity<Field> getItem(@PathVariable String itemId) {

        Field field=fieldService.getFieldById(itemId);

        return ResponseEntity.ok(field);
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
