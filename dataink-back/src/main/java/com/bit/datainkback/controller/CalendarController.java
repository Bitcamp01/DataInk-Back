package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.repository.ProjectEndDateRepository;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.UserProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/calendar")
public class CalendarController {
    @Autowired
    private UserProjectRepository userProjectRepository;

    @GetMapping("/project-end-dates")
    public List<UserProjectDto> getProjectEndDates(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();

        return userProjectRepository.findByUserUserId(userId).stream()
                .map(userProject -> UserProjectDto.builder()
                        .userId(userProject.getUser().getUserId())
                        .projectDto(ProjectDto.builder()
                                .name(userProject.getProject().getName())
                                .endDate(userProject.getProject().getEndDate())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }
}
