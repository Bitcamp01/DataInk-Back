package com.bit.datainkback.service.mongo;

import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.repository.mongo.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    // 폴더 생성
    public Folder createFolder(Folder folder) {
        return folderRepository.save(folder);
    }

    // 특정 폴더 조회
    public Folder getFolderById(String id) {
        return folderRepository.findById(id).orElse(null);
    }

    // 모든 폴더 조회
    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }

    // 폴더 삭제
    public void deleteFolder(String id) {
        folderRepository.deleteById(id);
    }

    // 폴더의 itemId 업데이트
    public Folder updateFolderFields(String folderId, String itemId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));
        folder.setItemId(itemId);
        return folderRepository.save(folder);
    }
}


