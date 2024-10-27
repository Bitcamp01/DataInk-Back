package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.entity.CustomUserDetails;
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
@RequestMapping("/workIn")
public class WorkInController {

    @Autowired
    private UserProjectRepository userProjectRepository;

    @GetMapping("/work-items")
    public List<UserProjectDto> getWorkItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // CustomUserDetails에서 userId를 가져옴
        Long userId = userDetails.getUser().getUserId(); // CustomUserDetails에서 직접 userId를 가져옴

        // 현재 유저 ID에 해당하는 프로젝트만 필터링하여 반환
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
