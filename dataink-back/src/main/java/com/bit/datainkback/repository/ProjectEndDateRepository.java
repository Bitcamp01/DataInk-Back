package com.bit.datainkback.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectEndDateRepository {
    @JsonProperty("end_date")
    private LocalDateTime endDate;

    public ProjectEndDateRepository(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
