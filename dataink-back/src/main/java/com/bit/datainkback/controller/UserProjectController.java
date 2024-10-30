package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.UserProjectDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.UserProject;
import com.bit.datainkback.service.UserProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping("/user-projects")
public class UserProjectController {
    @Autowired
    private UserProjectService userProjectService;

    @GetMapping("/projects")
    public ResponseEntity<?> getUserProjectDetails(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();

        ResponseDto<Map<String, List<?>>> responseDto = new ResponseDto<>();
        try {
            // User ID로 참여한 Project와 UserProject 정보 목록 조회
            List<ProjectDto> projectDtos = userProjectService.getProjectDtosByUserId(userId);
            List<UserProjectDto> userProjectDtos = userProjectService.getUserProjectDtosByUserId(userId);

            // 결과를 Map으로 분리하여 반환
            Map<String, List<?>> responseData = new HashMap<>();
            responseData.put("projects", projectDtos);
            responseData.put("userProjects", userProjectDtos);

            // 성공적인 응답 설정
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("사용자의 프로젝트 상세 목록 조회 성공.");
            responseDto.setItem(responseData);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            // 예외 처리
            log.error("사용자 프로젝트 상세 조회 중 오류 발생: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("사용자 프로젝트 상세를 조회하는 동안 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @PutMapping("/bookmark/{projectId}")
    public ResponseEntity<?> updateBookmarkStatus(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long projectId,
            @RequestBody UserProjectDto requestDto) {

        // 사용자 ID 가져오기
        Long userId = customUserDetails.getUser().getUserId();
        ResponseDto<UserProjectDto> responseDto = new ResponseDto<>();

        try {
            // DTO에서 북마크 상태를 가져옴
            boolean isBookmarked = requestDto.isBookmarked();

            // 서비스 호출로 업데이트 처리
            UserProjectDto savedBookmarked = userProjectService.updateBookmarkStatus(userId, projectId, isBookmarked);

            // 성공적인 응답 설정
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("북마크 상태가 성공적으로 업데이트되었습니다.");
            responseDto.setItem(savedBookmarked);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            // 기타 예외 처리
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("북마크 상태 업데이트에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @GetMapping
    public ResponseEntity<?> getProjectsBySearch(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                 @RequestParam("searchCondition") String searchCondtion,
                                                 @RequestParam("searchKeyword") String searchKeyword,
                                                 @RequestParam(value = "startDate", required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                 @RequestParam(value = "endDate", required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                 @PageableDefault(page = 0, size = 10) Pageable pageable) {
        ResponseDto<ProjectDto> responseDto = new ResponseDto<>();
        Long loggedInUserId = customUserDetails.getUser().getUserId();

        try {
            Page<ProjectDto> projectDtoList = userProjectService.findAll(searchCondtion, searchKeyword, pageable, startDate, endDate, loggedInUserId);
            log.info("projectDtoList: {}", projectDtoList);

            responseDto.setPageItems(projectDtoList);
            responseDto.setItem(ProjectDto.builder()
                    .searchCondition(searchCondtion)
                    .searchKeyword(searchKeyword)
                    .build());
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch(Exception e) {
            log.error("getProjectsBySearch error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

}
