package com.bit.datainkback.entity.mongo;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "mongo_project_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MongoProjectData {
    @Id
    private String id;  // MongoDB에서 자동 생성된 프로젝트 ID
    private Long projectId;  // RDBMS의 Project ID를 연동
    private List<Folder> folders;
}

