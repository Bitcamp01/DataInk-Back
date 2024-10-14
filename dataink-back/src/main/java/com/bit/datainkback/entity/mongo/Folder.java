package com.bit.datainkback.entity.mongo;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Folder {
    @Id
    private String id;
    private String label;
    private String itemId;  // 폴더일 경우 null, 파일(Task)일 경우 Task ID
    private String lastModifiedUserId;
    private String lastModifiedDate;
    private boolean isFolder;  // 폴더 또는 파일(Task) 여부
    private List<Folder> children;  // 하위 폴더 및 파일(Task) 통합
    private List<Field> fields;  // 폴더 단위로 fields를 저장
}

class Field {
    private String fieldName;
    private String fieldValue;
}
