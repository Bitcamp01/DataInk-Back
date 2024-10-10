package com.bit.datainkback.entity.mongo;


import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "project_structure")
public class ProjectStructure {
    @Id
    private String id;
    private String label;
    @DBRef
    private List<ProjectStructure> children;
    private String itemId;
    private String lastModifiedUserId;
    private LocalDateTime lastModifiedDate;
    private boolean isFolder;
    private String parentId;
    private boolean finished;
}
