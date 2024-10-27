package com.bit.datainkback.entity;

import com.bit.datainkback.dto.ProjectDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "PROJECT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@SequenceGenerator(
        name = "projectSeqGenerator",
        sequenceName = "PROJECT_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator ="projectSeqGenerator")
    @Column(name = "project_id")
    private Long projectId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Project와 UserProject는 일대다 관계
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonIgnore // 순환 참조 방지
    private List<UserProject> userProjects;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "mongo_data_id")
    private String mongoDataId;

    public ProjectDto toDto() {
        return ProjectDto.builder()
                .projectId(this.projectId)
                .userId(this.user.getUserId())
                .name(this.name)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .description(this.description)
                .mongoDataId(this.mongoDataId).build();
    }
}
