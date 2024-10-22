package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.MemberManagementService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberManagementServiceImpl implements MemberManagementService {
    private final UserRepository userRepository;

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> {
                    UserDto dto = user.toDto();
                    dto.setPassword(""); // password를 빈 문자열로 설정
                    return dto;
                });
    }

}
