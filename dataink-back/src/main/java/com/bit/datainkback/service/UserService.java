package com.bit.datainkback.service;

import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.entity.User;

import java.util.List;

import java.util.Map;

public interface UserService {
    Map<String, String> idCheck(String userId);

    Map<String, String> telCheck(String tel);

    UserDto join(UserDto userDto);

    UserDto login(UserDto userDto);

    User getUserById(Long userId);

    void changePassword(Long id, String currentPassword, String newPassword);
}