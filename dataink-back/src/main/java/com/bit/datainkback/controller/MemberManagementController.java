package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.service.ProjectService;
import com.bit.datainkback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor

public class MemberManagementController {

    private final UserService userService;
//    private final ProjectService projectService;

//    @GetMapping
//    public Page<UserDto> getAllUsers(Pageable pageable) {
//        return userService.getAllUsers(pageable);
//    }

//
//    @GetMapping
//    public Page<ProjectDto> getUsersByProjects(Pageable pageable, Long projectId) {
//        return projectService.getUsersByProjects(pageable, projectId);
//    }
}