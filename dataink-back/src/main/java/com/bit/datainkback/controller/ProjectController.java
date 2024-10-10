package com.bit.datainkback.controller;


import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.service.ProjectService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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


    @GetMapping
    public ResponseEntity<?> getInitFolderData(){
        ResponseDto responseDto = new ResponseDto();

    }
}
