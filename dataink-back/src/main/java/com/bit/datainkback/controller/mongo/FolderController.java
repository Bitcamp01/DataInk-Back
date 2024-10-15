package com.bit.datainkback.controller.mongo;

import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.service.mongo.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mongo/folders")
public class FolderController {
    @Autowired
    private FolderService folderService;

    // 폴더 생성
    @PostMapping
    public Folder createFolder(@RequestBody Folder folder) {
        return folderService.createFolder(folder);
    }

    // 특정 폴더 조회
    @GetMapping("/{id}")
    public Folder getFolderById(@PathVariable Long id) {
        return folderService.getFolderById(id);
    }

    // 모든 폴더 조회
    @GetMapping
    public List<Folder> getAllFolders() {
        return folderService.getAllFolders();
    }

    // 폴더 삭제
    @DeleteMapping("/{id}")
    public void deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
    }

    // 폴더의 fields 업데이트
//    @PutMapping("/{id}/fields")
//    public Folder updateFolderFields(@PathVariable String id, @RequestBody List<Field> fields) {
//        return folderService.updateFolderFields(id, fields);
//    }
}
