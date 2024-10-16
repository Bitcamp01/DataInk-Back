package com.bit.datainkback.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tasks {
    @Id
    private String id;  // Task의 고유 ID
    private String taskName;  // Task 이름
    private String parentFolderId;  // 상위 폴더 ID
    private List<String> itemIds;  // 연결된 필드 ID
    private String status;  // 작업 상태 (in_progress, submitted 등)
    private Map<String, Object> fieldValue;  // 필드값들
}
