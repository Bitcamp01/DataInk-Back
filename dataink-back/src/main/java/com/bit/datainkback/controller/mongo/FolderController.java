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
