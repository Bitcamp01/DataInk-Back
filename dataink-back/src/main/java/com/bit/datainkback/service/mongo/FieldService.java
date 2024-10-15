package com.bit.datainkback.service.mongo;

import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.repository.mongo.FieldRepository;
import com.bit.datainkback.repository.mongo.FolderRepository;
import com.bit.datainkback.repository.mongo.MongoProjectDataRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldService {

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private MongoProjectDataRepository mongoProjectDataRepository;

    // 폴더에 필드 추가
    public void addFieldsToTask(String folderId, List<Field> fields) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        // Task일 경우 (isFolder = false) 필드 추가
        if (!folder.isFolder()) {
            for (Field field : fields) {
                fieldRepository.save(field);  // 필드 저장
                folder.setItemId(field.getId().toString());  // itemId로 필드 연결
            }
            folder.setWorkstatus("진행중");  // 상태 설정
        } else {
            throw new IllegalArgumentException("폴더가 아닌 Task에만 필드를 추가할 수 있습니다.");  // 예외 처리
        }

        folderRepository.save(folder);  // 변경된 폴더(Task) 저장
    }





    // 특정 필드 조회
    public Field getFieldById(Long id) {
        return fieldRepository.findById(id).orElse(null);
    }
}



