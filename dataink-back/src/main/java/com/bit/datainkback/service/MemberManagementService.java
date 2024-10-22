package com.bit.datainkback.service;

import com.bit.datainkback.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberManagementService {

    Page<UserDto> getAllUsers(Pageable pageable);
}
