package com.bit.datainkback.service.mongo;

import com.bit.datainkback.dto.mongo.FolderDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.mongo.FolderRepository;
import com.bit.datainkback.repository.mongo.MongoProjectDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
    public Folder updateFolderFields(String folderId, List<String> itemIds) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));
        folder.setItemIds(itemIds);
        return folderRepository.save(folder);
    }

    // 폴더 ID로 폴더 트리 조회
    public List<FolderDto> getFolderTreeByIds(List<String> folderIds) {
        // 1. 폴더 ID를 사용하여 폴더 데이터 조회
        List<Folder> folders = folderRepository.findByIdIn(folderIds);

        // 2. 폴더 데이터를 트리 형태로 변환
        return buildFolderTree(folders);
    }

    // 트리 구조로 변환하는 메서드
    private List<FolderDto> buildFolderTree(List<Folder> folders) {
        List<FolderDto> folderTree = new ArrayList<>();

        for (Folder folder : folders) {
            // 상위 폴더부터 트리 구조를 구축
            FolderDto folderDto = folder.toDto();

            // children이 null이면 빈 리스트로 설정
            if (folder.getChildren() != null) {
                folderDto.setChildren(buildFolderTree(folder.getChildren()));
            } else {
                folderDto.setChildren(Collections.emptyList());
            }

            folderTree.add(folderDto);
        }
        return folderTree;
    }

    public void modifyFolder(String newName, String selectedFolder) {
        Folder folder = folderRepository.findById(selectedFolder)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));
        folder.setLabel(newName);
        folderRepository.save(folder);
    }

    public void updateFolder(Folder folder) {
        folderRepository.save(folder);
    }
}


