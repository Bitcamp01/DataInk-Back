package com.bit.datainkback.service;

import org.springframework.stereotype.Service;

import java.util.Map;

public interface UserService {

    Map<String, String> idCheck(String id);
}
