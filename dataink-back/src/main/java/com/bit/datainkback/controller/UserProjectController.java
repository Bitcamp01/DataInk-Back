package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.UserProject;
import com.bit.datainkback.service.UserProjectService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping("/user-projects")
public class UserProjectController {
    @Autowired
    private UserProjectService userProjectService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserProjects(@PathVariable Long userId) {
        ResponseDto<ProjectDto> responseDto = new ResponseDto<>();
        try {
            // User ID로 참여한 Project 목록 조회
            List<ProjectDto> joinedProjects = userProjectService.getUserProjectsByUserId(userId);

            // 성공적인 응답 설정
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("사용자의 프로젝트 목록 조회 성공.");
            responseDto.setItems(joinedProjects);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            // 예외 처리
            log.error("사용자 프로젝트 조회 중 오류 발생: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("사용자 프로젝트를 조회하는 동안 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

}
