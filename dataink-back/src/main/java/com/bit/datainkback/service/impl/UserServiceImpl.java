package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.enums.AuthenType;
import com.bit.datainkback.jwt.JwtProvider;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public Map<String, String> idCheck(String id) {
        Map<String, String> userCheckMsgMap = new HashMap<>();

        long usernameCheck = userRepository.countById(id);

        if(usernameCheck == 0)
            userCheckMsgMap.put("usernameCheckMsg", "available username");
        else
            userCheckMsgMap.put("usernameCheckMsg", "invalid username");

        return userCheckMsgMap;
    }

    @Override
    public UserDto join(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setAuthen(AuthenType.ROLE_USER);
        userDto.setRegdate(new Timestamp(System.currentTimeMillis()));
        userDto.setStatus("active");

        UserDto joinedUserDto = userRepository.save(userDto.toEntity()).toDto();

        joinedUserDto.setPassword("");

        return joinedUserDto;
    }

    @Override
    public UserDto login(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new RuntimeException("id not exist")
        );

        if(!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("wrong password");
        }

        UserDto loginUserDto = user.toDto();
        loginUserDto.setPassword("");
        loginUserDto.setToken(jwtProvider.createJwt(user));

        return loginUserDto;
    }

}
