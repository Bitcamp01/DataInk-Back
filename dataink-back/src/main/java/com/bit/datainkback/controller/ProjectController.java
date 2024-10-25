package com.bit.datainkback.controller;


import com.bit.datainkback.common.FileUtils;
import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.mongo.FolderDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.service.FileService;
import com.bit.datainkback.service.ProjectService;
import com.bit.datainkback.service.mongo.FieldService;
import com.bit.datainkback.service.mongo.FolderService;
import com.bit.datainkback.service.mongo.MongoLabelTaskService;
import com.bit.datainkback.service.mongo.MongoProjectDataService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private MongoLabelTaskService mongoLabelTaskService;

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
        //별도의 폴더를 생성
        folderService.createFolder(newFolder);

        //프로젝트 바로 아래에 붙는 폴더는 projects의 folders에 ids로 저장
        if (selectedFolder.equals(selectedProject.toString())){
            MongoProjectData projectData = projectService.getProjectDataById(selectedProject);
            projectData.getFolders().add(newFolder.getId());  // 폴더 ID 추가
            projectService.updateProjectData(projectData);
        }
        // 그 아래 붙는 폴더는 folders에 생성하고 부모 폴더를 찾아 부모 폴더 children배열에 추가
        else {
            folderService.addNewFolder(selectedFolder,newFolder);   
        }

        return ResponseEntity.ok(newFolder.toDto());  // 필요시 적절한 DTO 반환
    }
    @PostMapping("/rename")
    public ResponseEntity<Folder> renameProject(@RequestBody Map<String, Object> requestData,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String label = (String) requestData.get("label");
        String selectedFolder = requestData.get("selectedFolder").toString();
        Long selectedProject = ((Number) requestData.get("selectedProject")).longValue();
        //프로젝트 이름 수정 
        if (selectedFolder.equals(selectedProject.toString())){
            Project project=projectService.modifyProjectName(label,selectedProject);
            //반환 정보를 folder형태로 받기 때문에 생성함
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
        //하위 폴더 이름 수정,
        //이때 프로젝트 바로 아래 수정은 그냥 적용하면 됨(상위 폴더에서 기억할 필요 없음)
        //그러나 그 하위 폴더의 경우 상위 폴더의 children배열에서 이름을 변경해줘야 함
        else {
            Folder folder=folderService.modifyFolderName(label,selectedFolder,customUserDetails.getUser().getUserId());
            return ResponseEntity.ok(folder);
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
            //프로젝트 삭제
            if (entry.getKey().equals(entry.getValue())) {
                //프로젝트에 붙은 최상위 폴더들
                List<Folder> childrenFolder = projectService.getProjectById(Long.parseLong(entry.getKey())).getFolders();
                //최상위 폴더들에 대한 정보를 가져왔으므로 각 최상위 폴더부터 전부 삭제 시도
                folderService.deleteFolderAndChildFolder(childrenFolder);
                //몽고 프로젝트 삭제
                mongoProjectDataService.deleteFolder(entry.getKey());
                //폴더 삭제 후 rdb프로젝트 삭제
                projectService.deleteProject(Long.parseLong(entry.getKey()));
            }
            //하위 폴더 삭제
            else {
                //삭제 될 폴더 가져옴
                Folder folder=folderService.getFolderById(entry.getKey());
                //삭제 될 폴더의 상위 폴더에서 children배열 제거
                folderService.deleteParentFolderChildren(folder);
                //삭제 시 하위 폴더까지 포함해서 제거, 이때 삭제 대상의 폴더의 children을 줌
                folderService.deleteFolderAndChildFolder(folder.getChildren());
                //삭제 대상이 된 폴더 제거
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
        while (!hasConversion.isEmpty()){
            String conversionId=conversionListIds.poll();
            Tasks tasks=mongoLabelTaskService.getTaskById(conversionId);

        }

        return ResponseEntity.ok("ok");

    }
    public static Map<String, Object> transformToNestedMap(Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        String contentValue = (String) data.get("content");

        // hierarchy 키들만 추출하여 리스트로 정렬
        String[] keys = data.keySet().stream()
                .filter(key -> key.startsWith("hierarchy"))
                .sorted()
                .toArray(String[]::new);

        buildNestedMap(result, data, keys, contentValue, 0);
        return result;
    }

    private static void buildNestedMap(Map<String, Object> currentMap, Map<String, Object> data, String[] keys, String contentValue, int depth) {
        String currentKey = (String) data.get(keys[depth]);

        // 마지막 계층인 경우 content 값 삽입
        if (depth == keys.length - 1) {
            currentMap.put(currentKey, contentValue);
            return;
        }

        // 하위 맵을 생성하거나 기존 맵을 가져옴
        Map<String, Object> nextMap = (Map<String, Object>) currentMap.getOrDefault(currentKey, new HashMap<>());
        currentMap.put(currentKey, nextMap);

        // 재귀적으로 다음 계층으로 이동
        buildNestedMap(nextMap, data, keys, contentValue, depth + 1);
    }
    @PostMapping("/upload")
    public ResponseEntity<List<Folder>> uploadfile(@RequestParam("selectedFolder") String selectedFolder,
                                             @RequestParam("selectedProject") Long selectedProject,
                                             @RequestParam("files") MultipartFile[] files,
                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<Folder> folders=new ArrayList<>();
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
            //폴더 하나 생성
            folderService.createFolder(newFolder);
            //프로젝트 바로 아래에 추가시-현재는 고려 X
            if (selectedFolder.equals(selectedProject.toString())){
                mongoProjectDataService.addFolderToProject(selectedProject,newFolder);
            }
            //하위 폴더에 추가
            else{
                folderService.addNewFolder(selectedFolder,newFolder);
                folders.add(newFolder);
            }
        }
        return ResponseEntity.ok(folders);

    }
    @PostMapping("/item_select")
    public ResponseEntity<String> folderItemSelect(@RequestBody Map<String, Object> requestData) {
        //선택한 항목 아이디
        String selectedItemId = requestData.get("selectedItemId").toString();
        //선택된
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
    //자르고 붙여넣기의 경우 자른 폴더의 상위 폴더에서 children배열에서 제거하고 붙여넣을 폴더의 children배열에 추가하는 방식
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
//                    if (split[0].equals(split[1])){} 프로젝트는 복사,자르기 막음
                    //붙여넣기 대상이 되는 폴더 가져옴
                    Folder folder=folderService.getFolderById(selectedFolder);
                    //자르기 대상이 되는 폴더의 상위 폴더에서 아이디 제거
                    folderService.deleteParentFolderChildren(folderService.getFolderById(split[0]));
                    folder.getChildren().add(folderService.getFolderById(split[0]));
                    folderService.updateFolder(folder);
                }
            }
        }
        return ResponseEntity.ok("ok");

    }
    @PostMapping("/copy_paste")
    public ResponseEntity<List<Folder>> copyPaste(@RequestParam("selectedFolder") String selectedFolder,
                                           @RequestParam("selectedProject") Long selectedProject,
                                           @RequestBody List<String> requestData) {
        log.info("잘라서 붙여넣기 할 데이터 {}",requestData.toString());
        List<Folder> folders=new ArrayList<>();
        //프로젝트 바로 아래에 붙여넣기
        if (selectedFolder.equals(selectedProject.toString())) {

        }
        else {
            for (String s:requestData){
                String[] split=s.split("_");
                if (split.length >=2){
//                    if (split[0].equals(split[1])){} 프로젝트는 복사,자르기 막음
                    //붙여넣기 대상이 되는 폴더 가져옴
                    Folder folder=folderService.getFolderById(selectedFolder);
                    //복사 대상이 된 폴더 하위 폴더까지 전부 복사
                    Folder copyFolder= folderService.copyFolder(split[0]);
                    log.info("copyfolder {}",copyFolder.toString());
                    //복사한 붙여넣기 할 폴더에 넣어줌
                    folder.getChildren().add(copyFolder);
                    folderService.updateFolder(folder);

                    folders.add(copyFolder);
                }
            }
        }
        return ResponseEntity.ok(folders);

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
            Folder updateFolder=folderService.getFolderById(selectedFolder);
            List<Folder> folders=new ArrayList<>();
            for (Folder folder:folderService.getFolderById(selectedFolder).getChildren()){
                Folder getFolder=folderService.getFolderById(folder.getId());
                if (getFolder == null){
//                    updateFolder.getChildren().stream().filter(x-> x.getId() != folder.getId());
                }
                else {
                    folders.add(getFolder);
                }
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
    @GetMapping("/pdf/{label}")
    public ResponseEntity<String> getPdfUrl(@PathVariable String label) {
        return ResponseEntity.ok(fileUtils.getPdfFileUrl(label));
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
