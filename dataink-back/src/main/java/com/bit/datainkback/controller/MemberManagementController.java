package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.service.MemberManagementService;
import com.bit.datainkback.service.MemberProjectService;
import com.bit.datainkback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberManagementController {

    @Autowired
    private final MemberProjectService memberProjectService;
    private final MemberManagementService memberManagementService;


    @GetMapping("/modal")
    public ResponseEntity<?> getUsersForModal(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        ResponseDto<UserDto> responseDto = new ResponseDto<>();

        try {
            // UserDto 페이지 가져오기
            Page<UserDto> userDtoPage = memberManagementService.getAllUsers(pageable);

            // ResponseDto에 데이터 세팅
            responseDto.setPageItems(userDtoPage); // Page에서 리스트를 가져옴
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            // 응답 반환
            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            log.error("Error fetching modal users: {}", e.getMessage());

            // 오류 발생 시 응답 세팅
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("Error fetching modal users");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    // 단일 API에서 탭별로 다른 데이터 반환
    @GetMapping
    public ResponseEntity<?> getTabData(
            @RequestParam(value = "tab") String tab,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        switch (tab) {
            case "users":
                ResponseDto<UserDto> responseDto = new ResponseDto<>();
                try {
                    Page<UserDto> userDtoList = memberManagementService.getAllUsers(pageable);

                    responseDto.setPageItems(userDtoList);
                    responseDto.setStatusCode(HttpStatus.OK.value());
                    responseDto.setStatusMessage("ok");

                    return ResponseEntity.ok(responseDto);
                } catch(Exception e) {
                    log.error("getAllUsers error: {}", e.getMessage());
                    responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    responseDto.setStatusMessage(e.getMessage());
                    return ResponseEntity.internalServerError().body(responseDto);
                }
            case "projects":
                ResponseDto<ProjectDto> responseDto2 = new ResponseDto<>();
                try {
                    Page<ProjectDto> projectDtoList = memberProjectService.getAllProjects(pageable);

                    responseDto2.setPageItems(projectDtoList);
                    responseDto2.setStatusCode(HttpStatus.OK.value());
                    responseDto2.setStatusMessage("ok");

                    return ResponseEntity.ok(responseDto2);
                } catch(Exception e) {
                    log.error("getAllProjects error: {}", e.getMessage());
                    responseDto2.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    responseDto2.setStatusMessage(e.getMessage());
                    return ResponseEntity.internalServerError().body(responseDto2);
                }
            default:
                throw new IllegalArgumentException("Invalid tab: " + tab);
        }
    }

}