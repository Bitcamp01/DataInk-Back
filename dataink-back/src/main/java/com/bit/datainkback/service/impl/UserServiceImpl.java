package com.bit.datainkback.service.impl;

import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository memberRepository;


    @Override
    public Map<String, String> idCheck(String id) {
        return Map.of();
    }
}
