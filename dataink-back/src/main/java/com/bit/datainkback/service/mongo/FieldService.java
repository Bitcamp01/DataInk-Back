package com.bit.datainkback.service.mongo;

import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.repository.mongo.FieldRepository;
import com.bit.datainkback.repository.mongo.FolderRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FieldService {

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Optional<Folder> findFolderById(String folderId, List<Folder> folders) {
        // folders가 null인 경우 빈 리스트로 초기화
        if (folders == null) {
            folders = new ArrayList<>();
        }

        for (Folder folder : folders) {
            // folder가 null인지 체크
            if (folder == null) {
                continue; // null인 경우 다음 폴더로 넘어감
            }

            // 현재 폴더의 ID와 비교
            if (folder.getId() != null && folder.getId().equals(folderId)) {
                return Optional.of(folder);
            }

            // 자식 폴더 탐색
            Optional<Folder> found = findFolderById(folderId, folder.getChildren());
            if (found.isPresent()) {
                return found; // 자식 폴더에서 발견
            }
        }
        return Optional.empty(); // 발견하지 못함
    }



    public void addFieldsToTask(String folderId, List<Field> fields) {
        // 자식 폴더 찾기
        Optional<Folder> optionalFolder = findFolderById(folderId, folderRepository.findAll());

        // 폴더가 발견되지 않은 경우 예외 처리
        if (!optionalFolder.isPresent()) {
            throw new RuntimeException("Folder not found");
        }

        Folder targetFolder = optionalFolder.get();

        // 폴더일 경우 (isFolder = true) 필드 추가
        if (targetFolder.isFolder()) {
            // 현재 folder의 itemId 리스트 가져오기 (배열로 처리)
            List<String> itemIds = targetFolder.getItemIds();
            if (itemIds == null) {
                itemIds = new ArrayList<>();
            }

            // 각 필드를 저장하고 itemIds 리스트에 추가
            for (Field field : fields) {
                fieldRepository.save(field);  // 필드 저장
                itemIds.add(field.getId().toString());  // itemId로 필드 연결
            }

            targetFolder.setItemIds(itemIds);  // 업데이트된 itemId 리스트 설정
        } else {
            throw new IllegalArgumentException("폴더가 아니면 항목을 추가할 수 없습니다.");
        }

        folderRepository.save(targetFolder);  // 변경된 폴더 저장
    }

    // 특정 필드 조회
    public Field getFieldById(String id) {
        return fieldRepository.findById(id).orElse(null);
    }
}