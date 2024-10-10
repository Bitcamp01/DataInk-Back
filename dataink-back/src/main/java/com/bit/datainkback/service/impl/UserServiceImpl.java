package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> idCheck(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        Map<String, String> response = new HashMap<>();
        if (userOptional.isPresent()) {
            response.put("available", "false");
            response.put("message", "ID is already taken.");
        } else {
            response.put("available", "true");
            response.put("message", "ID is available.");
        }
        return response;
    }

    @Override
    public UserDto join(UserDto userDto) {

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        UserDto joinedUserDto = userRepository.save(userDto.toEntity()).toDto();

        joinedUserDto.setPassword("");

        return joinedUserDto;
    }

    @Override
    public UserDto login(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new RuntimeException("Id not exist")
        );

        if (!userDto.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("wrong password");
        }

        UserDto loginUserDto = user.toDto();
        loginUserDto.setPassword("");

        return loginUserDto;
    }

    @Override
    public void logout() {

    }
}
