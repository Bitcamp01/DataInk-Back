package com.bit.datainkback.dto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.mongo.Folder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProjectDto {
    private Long projectId;
    private Long userId;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String mongoDataId;
    // MongoDB용 폴더 구조를 포함한 필드
    private List<Folder> folders;

    public Project toEntity(User user) {
        return Project.builder()
                .projectId(this.projectId)
                .user(user)
                .name(this.name)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .description(this.description)
                .mongoDataId(this.mongoDataId).build();
    }
}

