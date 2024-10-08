package com.bit.datainkback.entity;

import com.bit.datainkback.dto.ProjectDto;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PROJECT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private int projectId;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "desc", length = 255)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "mongo_data_id", length = 255)
    private String mongoDataId;
    public ProjectDto toDto() {
        return ProjectDto.builder().projectId(this.projectId).userId(this.owner.getUserId())
                .startDate(this.startDate).endDate(this.endDate).description(this.description)
                .mongoDataId(this.mongoDataId).build();
    }
}
