package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
import com.bit.datainkback.jwt.JwtProvider;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public Map<String, String> idCheck(String id) {
        Map<String, String> userCheckMsgMap = new HashMap<>();
        long usernameCheck = userRepository.countById(id);

        if (usernameCheck == 0)
            userCheckMsgMap.put("usernameCheckMsg", "available username");
        else
            userCheckMsgMap.put("usernameCheckMsg", "invalid username");

        return userCheckMsgMap;
    }

    @Override
    public Map<String, String> telCheck(String tel) {
        Map<String, String> telCheckMsgMap = new HashMap<>();
        long telCheck = userRepository.countByTel(tel);

        if(telCheck == 0)
            telCheckMsgMap.put("telCheckMsg", "available tel");
        else
            telCheckMsgMap.put("telCheckMsg", "invalid tel");

        return telCheckMsgMap;
    }

    @Override
    public UserDto join(UserDto userDto) {
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        userDto.setRegdate(new Timestamp(System.currentTimeMillis()));
        userDto.setStatus("active");

        User user = userDto.toEntity();
        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);
        user.setUserDetail(userDetail);

        UserDto joinedUserDto = userRepository.save(user).toDto();
        joinedUserDto.setPassword(""); // 비밀번호는 클라이언트로 반환하지 않음

        return joinedUserDto;
    }

    @Override
    public UserDto login(UserDto userDto) {
        // 소셜 로그인 처리
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // 비밀번호 검증을 하지 않고 로그인 처리
            UserDto loginUserDto = user.toDto();
            loginUserDto.setPassword("");
            loginUserDto.setToken(jwtProvider.createJwt(user));
            return loginUserDto;
        }

        // 일반 로그인 처리
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User ID does not exist"));

        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        UserDto loginUserDto = user.toDto();
        loginUserDto.setPassword("");
        loginUserDto.setToken(jwtProvider.createJwt(user));

        return loginUserDto;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for userId: " + userId));
    }

    public void changePassword(Long loggedInUserId, String currentPassword, String newPassword) {
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void saveUsers(List<User> users) {
        userRepository.saveAll(users);
    }
}
