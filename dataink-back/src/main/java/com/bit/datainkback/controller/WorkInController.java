package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.entity.CustomUserDetails;
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
public class WorkInController {

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/work-items")
    public List<ProjectDto> getWorkItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // CustomUserDetails에서 userId를 가져옴
        Long userId = userDetails.getUser().getUserId(); // CustomUserDetails에서 직접 userId를 가져옴

        // 현재 유저 ID에 해당하는 프로젝트만 필터링하여 반환
        return projectRepository.findByUser_UserId(userId).stream()
                .map(project -> ProjectDto.builder()
                        .userId(project.getUser().getUserId())
                        .name(project.getName())
                        .endDate(project.getEndDate()) // 종료일 추가
                        .build())
                .collect(Collectors.toList());
    }
}
