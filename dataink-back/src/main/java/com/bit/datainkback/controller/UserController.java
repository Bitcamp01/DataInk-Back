package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/id-check")
    public ResponseEntity<?> idCheck(@RequestBody UserDto userDto) {
        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();

        try {
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

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserDto userDto) {
        ResponseDto<UserDto> responseDto = new ResponseDto<>();

        try {
            UserDto joinedUserDto = userService.join(userDto);
            responseDto.setStatusCode(HttpStatus.CREATED.value());
            responseDto.setStatusMessage("created");
            responseDto.setItem(joinedUserDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            log.error("join error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        ResponseDto<UserDto> responseDto = new ResponseDto<>();

        try {
            UserDto loginUserDto = userService.login(userDto);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            responseDto.setItem(loginUserDto);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("login error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>();

        try {
            userService.logout();
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            responseDto.setItem(Map.of("message", "logout success"));

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("logout error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }
}
