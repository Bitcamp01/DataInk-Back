package com.bit.datainkback.controller;

import com.bit.datainkback.dto.*;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberManagementController {

    private final MemberProjectService memberProjectService;
    private final MemberManagementService memberManagementService;
    private final UserProjectService userProjectService;




    // 특정 프로젝트의 멤버 리스트 조회
    @GetMapping("/modal-project/{projectId}")
    public ResponseEntity<?> getProjectMembers(@PathVariable("projectId") Long projectId) {
        ResponseDto<UserDto> responseDto = new ResponseDto<>();
        try {
            List<UserDto> members = userProjectService.getMembersByProjectId(projectId);
            responseDto.setItems(members);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Error fetching project members for projectId {}: {}", projectId, e.getMessage());

            // 오류 발생 시 응답 세팅
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("Error fetching project members");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    // 프로젝트에 멤버 변경사항 저장
    @PostMapping("/modal-save/{projectId}")
    public ResponseEntity<List<UserProjectDto>> saveProjectMembers(
            @PathVariable("projectId") Long projectId,
            @RequestBody ProjectMemberUpdateRequestDto requestDto) {

        // projectId가 일치하는지 검증 (NullPointerException 방지)
        if (!Objects.equals(requestDto.getProjectId(), projectId)) {
            return ResponseEntity.badRequest().build();
        }

        // 요청 DTO의 projectId를 명확히 설정
        requestDto.setProjectId(projectId);

        //서비스 레이어에서 변경사항 처리
        List<UserProjectDto> updatedMembers  = userProjectService.updateProjectMembers(requestDto);
        return ResponseEntity.ok(updatedMembers);
    }

//    // 프로젝트에서 멤버 삭제
//    @DeleteMapping("/modal-delete")
//    public ResponseEntity<Void> removeMembersFromProject(@PathVariable Long projectId, @RequestBody List<Long> userIds) {
//        userProjectService.removeMembersFromProject(projectId, userIds);
//        return ResponseEntity.ok().build();
//    }

    //프로젝트 별 참여자 가져오기
    @GetMapping("/joined-projects/{projectId}")
    public ResponseEntity<?> joinedProjects(@PathVariable("projectId") Long projectId) {
        ResponseDto<String> responseDto = new ResponseDto<>();

        try {
            List<String> joinedUserNames = userProjectService.getJoinedUserIds(projectId);

            // ResponseDto에 데이터 세팅
            responseDto.setItems(joinedUserNames);
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


    //모달에서 모든 멤버 가져오기
    //            @PageableDefault(page = 0, size = 10) Pageable pageable
    @GetMapping("/modal")
    public ResponseEntity<?> getUsersForModal(Pageable pageable) {
        ResponseDto<UserDto> responseDto = new ResponseDto<>();

        try {
            // UserDto 페이지 가져오기
            Page<UserDto> userDtoPage = memberManagementService.getAllUsers(pageable);
            System.out.println(userDtoPage.getContent().size());
            userDtoPage.getContent().forEach(user -> System.out.println(user));


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