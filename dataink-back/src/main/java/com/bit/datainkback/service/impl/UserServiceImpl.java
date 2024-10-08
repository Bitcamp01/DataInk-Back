package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
        User user = User.builder()
                .id(userDto.getId())
                .password(userDto.getPassword())
                .name(userDto.getName())
                .build();

        User savedUser = userRepository.save(user);

        return UserDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .build();
    }

    @Override
    public UserDto login(UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(userDto.getPassword())) {
                return UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build();
            } else {
                throw new RuntimeException("Invalid password.");
            }
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    @Override
    public void logout() {
    }
}