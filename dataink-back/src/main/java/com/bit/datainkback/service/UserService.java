package com.bit.datainkback.service;

import com.bit.datainkback.dto.UserDto;
import java.util.Map;

public interface UserService {
    Map<String, String> idCheck(String userId);

    Map<String, String> telCheck(String tel);

    UserDto join(UserDto userDto);

    UserDto login(UserDto userDto);

    void changePassword(Long id, String currentPassword, String newPassword);
}