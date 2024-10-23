package com.bit.datainkback.controller.mongo;

import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.mongo.FolderDto;
import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.repository.mongo.FieldRepository;
import com.bit.datainkback.repository.mongo.FolderRepository;
import com.bit.datainkback.service.mongo.FieldService;
import com.bit.datainkback.service.mongo.FolderService;
import com.bit.datainkback.service.mongo.MongoProjectDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mongo/folders")
public class FolderController {
    @Autowired
    private FolderService folderService;

    @Autowired
    private MongoProjectDataService mongoProjectDataService;

    @Autowired
    private FieldService fieldService;

    // 폴더 생성
    @PostMapping
    public Folder createFolder(@RequestBody Folder folder) {
        return folderService.createFolder(folder);
    }

    // 특정 폴더 조회
    @GetMapping("/{id}")
    public Folder getFolderById(@PathVariable String id) {
        return folderService.getFolderById(id);
    }

    // 모든 폴더 조회
//    @GetMapping
//    public List<Folder> getAllFolders() {
//        return folderService.getAllFolders();
//    }
    @GetMapping
    public ResponseEntity<?> getFolders(
            @RequestParam(value = "category1", required = false) String category1,
            @RequestParam(value = "category2", required = false) String category2,
            @RequestParam(value = "category3", required = false) String category3,
            @RequestParam(value = "workStatus", required = false) String workStatus,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        ResponseDto<FolderDto> responseDto = new ResponseDto<>();

        try {
            Page<FolderDto> folderList = folderService.findAll(category1, category2, category3, workStatus, page, size);

            responseDto.setPageItems(folderList);
            responseDto.setCurrentPage(page);
            responseDto.setPageSize(size);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("getFolders error: {}", e.getMessage());
            responseDto.setStatusCode(Http








                    Status.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }




    // 폴더 삭제
    @DeleteMapping("/{id}")
    public void deleteFolder(@PathVariable String id) {
        folderService.deleteFolder(id);
    }

    // 특정 프로젝트에 폴더 추가
    @PostMapping("/projects/{projectId}")
    public ResponseEntity<?> addFolderToProject(@PathVariable Long projectId, @RequestBody List<Folder> folders) {
        for (Folder folder : folders) {
            mongoProjectDataService.addFolderToProject(projectId, folder);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
