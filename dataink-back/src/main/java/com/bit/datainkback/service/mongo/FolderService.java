package com.bit.datainkback.service.mongo;

import com.bit.datainkback.dto.mongo.FolderDto;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.MongoProjectData;
import com.bit.datainkback.repository.LabelTaskRepository;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.mongo.FolderRepository;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import com.bit.datainkback.repository.mongo.MongoProjectDataRepository;
import com.bit.datainkback.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private MongoProjectDataRepository mongoProjectDataRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoLabelTaskRepository mongoLabelTaskRepository;
    @Autowired
    private LabelTaskRepository labelTaskRepository;

    // 폴더 생성
    public Folder createFolder(Folder folder) {
        return folderRepository.save(folder);
    }

    // 특정 폴더 조회
    public Folder getFolderById(String id) {
        Folder folder= folderRepository.findById(id).orElse(null);
        return folder;
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
    public FolderDto getFolderTree(String folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));

        FolderDto folderDto = new FolderDto();
        folderDto.setId(folder.getId());
        folderDto.setLabel(folder.getLabel());
        folderDto.setLastModifiedDate(folder.getLastModifiedDate());
        folderDto.setChildren(new ArrayList<>());

        if (folder.getChildren() != null && !folder.getChildren().isEmpty()) {
            for (Folder childFolder : folder.getChildren()) {
                FolderDto childFolderDto = getFolderTree(childFolder.getId());
                folderDto.getChildren().add(childFolderDto);
            }
        }

        return folderDto;
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

    public Folder modifyFolderName(String newName, String selectedFolder, Long userId) {


        // 이름이 변경된 폴더 가져옴
        Folder folder = folderRepository.findById(selectedFolder).orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다"));
        // 하위 폴더 이름 및 수정자 정보 업데이트
        folder.setLabel(newName);
        folder.setLastModifiedUserId(userId.toString());
        folder.setLastModifiedDate(LocalDateTime.now().toString());

        return folderRepository.save(folder);
    }

    public void updateFolder(Folder folder) {
        folderRepository.save(folder);
    }
    //생성된 폴더를 부모 폴더 children배열에 추가 해줌
    public void addNewFolder(String parentId,Folder folder) {
        Folder parentFolder=folderRepository.findById(parentId).orElseThrow(()-> new RuntimeException("부모 폴더를 찾을 수 없습니다"));
        parentFolder.getChildren().add(folder);
        folderRepository.save(parentFolder);
    }
    //하위 폴더 포함 삭제, 이때 상위 폴더에서 children배열은 건들지 않음
    public void deleteFolderAndChildFolder(List<Folder> childrenFolder) {
        for (Folder folder : childrenFolder) {
            Folder subFolder= folderRepository.findById(folder.getId()).orElseThrow(()->new RuntimeException("not found folder"));
            if (!subFolder.isFolder()){
                mongoLabelTaskRepository.deleteById(subFolder.getId());
                labelTaskRepository.deleteByRefTaskId(subFolder.getId());
            }
            if (subFolder.getChildren() != null && !subFolder.getChildren().isEmpty()) {
                deleteFolderAndChildFolder(subFolder.getChildren());
            }
            folderRepository.delete(folder);
        }
    }

    //삭제 대상이 되는 폴더를 넘김, 그러면 상위 폴더의 children을 수정, 폴더는 삭제 하지 않음
    public void deleteParentFolderChildren(Folder folder) {
        // 삭제될 폴더의 상위 폴더를 가져옴
        Folder parentFolder = folderRepository.findByChildrenId(folder.getId()).orElse(null);
        //상위 폴더가 프로젝트임
        if (parentFolder == null) {
            //프로젝트 folders에 있는 아이디 목록 갱신
            MongoProjectData mongoProjectData=mongoProjectDataRepository.findByFolders(folder.getId());
            List<String> updateFolderIds= mongoProjectData.getFolders().stream().filter(childFolder -> !childFolder.equals(folder.getId())).collect(Collectors.toList());
            mongoProjectData.setFolders(updateFolderIds);
            mongoProjectDataRepository.save(mongoProjectData);
        }
        //상위 폴더가 폴더임
        else{
            List<Folder> parentFolderChild = parentFolder.getChildren();
            //상위 폴더의 자식 배열에서 수정되는 폴더를 삭제
            List<Folder> updatedChildren = parentFolderChild.stream()
                    .filter(childFolder -> !childFolder.getId().equals(folder.getId()))
                    .collect(Collectors.toList());
            parentFolder.setChildren(updatedChildren);
            folderRepository.save(parentFolder);
        }
        
    }
    // 최상위 폴더 복사 메서드
    public Folder copyFolder(String folderId) {
        Folder originalFolder = folderRepository.findById(folderId).get();
        if (originalFolder == null) {
            throw new RuntimeException("Folder not found with id: " + folderId);
        }

        // 최상위 폴더 복사 (자식도 재귀적으로 복사)
        Folder copiedFolder = recursiveCopyFolder(originalFolder);
        return folderRepository.save(copiedFolder);  // MongoDB에 저장
    }
    public String getOriginalFileName(String fileName) {
        // 언더스코어(_)를 기준으로 파일 이름을 분리
        String[] parts = fileName.split("_");

        // 세 번째 요소부터 끝까지 결합하여 실제 파일 이름을 생성
        StringBuilder originalFileName = new StringBuilder();
        for (int i = 2; i < parts.length; i++) {
            originalFileName.append(parts[i]);
            if (i < parts.length - 1) {
                originalFileName.append("_"); // 중간에 언더스코어 추가
            }
        }

        return originalFileName.toString();
    }

    // 재귀적 복사 메서드
    private Folder recursiveCopyFolder(Folder folderToCopy) {
        // 새 폴더 생성 및 필드 복사
        Folder newFolder = new Folder();
        newFolder.generateId();
        newFolder.setLabel(folderToCopy.getLabel());
        newFolder.setItemIds(folderToCopy.getItemIds());
        newFolder.setLastModifiedUserId(folderToCopy.getLastModifiedUserId());
        newFolder.setLastModifiedDate(folderToCopy.getLastModifiedDate());
        newFolder.setFolder(folderToCopy.isFolder());
        newFolder.setFinished(folderToCopy.isFinished());
        if (!folderToCopy.isFolder()){
            String realFileName = getOriginalFileName(folderToCopy.getLabel());
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date nowDate = new Date();
            String nowDateStr = format.format(nowDate);
            UUID uuid = UUID.randomUUID();
            String uniqueName=uuid.toString() + "_" + nowDateStr + "_" + realFileName;
            fileService.copyFile(folderToCopy.getLabel(),uniqueName);
            newFolder.setLabel(uniqueName);
        }
        // 자식 폴더 복사
        List<Folder> copiedChildren = new ArrayList<>();
        if (folderToCopy.getChildren() != null) {
            for (Folder child : folderToCopy.getChildren()) {
                Folder copiedChild = recursiveCopyFolder(child);
                copiedChildren.add(copiedChild);
            }
        }
        newFolder.setChildren(copiedChildren);
        folderRepository.save(newFolder);
        return newFolder;
    }
}


