package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.repository.ProjectEndDateRepository;
import com.bit.datainkback.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://dataink.site")
public class CalendarController {

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/project-end-dates")
    public List<ProjectDto> getProjectEndDates(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();

        return projectRepository.findByUser_UserId(userId).stream()
                .map(project -> ProjectDto.builder()
                        .userId(project.getUser().getUserId())
                        .endDate(project.getEndDate())
                        .name(project.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
