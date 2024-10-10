package com.bit.datainkback.dto.mongo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProjectStructureDto {
    private String id;
    private String label;
    private boolean isFolder;
    private String itemId;
    private Long lastModifiedUserId;
    private LocalDateTime lastModifiedDate;
    private List<ProjectStructureDto> children;
    private Boolean finished;
}
