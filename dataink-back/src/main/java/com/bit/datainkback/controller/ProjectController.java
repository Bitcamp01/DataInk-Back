package com.bit.datainkback.controller;


import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.mongo.ProjectStructureDto;
import com.bit.datainkback.service.ProjectService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;


    // 프로젝트 정보가 아니고 전체 프로젝트 구조 정보를 가져오는 메소드
    @GetMapping
    public ResponseEntity<?> getInitFolderData(){
        ResponseDto<ProjectStructureDto> responseDto = new ResponseDto();
        try {
            ProjectStructureDto projectStructureDto=projectService.getInitFolderData();
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            log.error("delete error: {}",e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }
}
