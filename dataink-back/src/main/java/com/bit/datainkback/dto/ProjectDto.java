package com.bit.datainkback.dto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private int projectId;
    private int userId;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String mongoDataId;

    public Project toEntity(User user) {
        return Project.builder().projectId(this.projectId).owner(user)
                .startDate(this.startDate).endDate(this.endDate).description(this.description)
                .mongoDataId(this.mongoDataId).build();
    }
}

