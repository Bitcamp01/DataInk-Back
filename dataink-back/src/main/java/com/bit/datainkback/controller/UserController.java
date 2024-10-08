package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private UserService userService;

    @PostMapping("/id-check")
    public ResponseEntity<?> idCheck(@RequestBody UserDto userDto) {
        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();

        try {
            log.info("id: {}", userDto.getId());
            Map<String, String> map = userService.idCheck(userDto.getId());

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            responseDto.setItem(map);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("username-check error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

}
