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
        long telCheck = userRepository.countByTel(tel); // 수정된 부분: findByTel -> countByTel

        if(telCheck == 0)
            telCheckMsgMap.put("telCheckMsg", "available tel");
        else
            telCheckMsgMap.put("telCheckMsg", "invalid tel");

        return telCheckMsgMap;
    }

    @Override
    public UserDto join(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setRegdate(new Timestamp(System.currentTimeMillis()));
        userDto.setStatus("active");

        // User 엔티티 생성
        User user = userDto.toEntity();

        // UserDetail을 user_id만 설정한 상태로 생성
        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);  // User와 연관 설정
        user.setUserDetail(userDetail);  // User에 UserDetail 설정

        // User 엔티티 저장 (UserDetail도 같이 저장됨)
        UserDto joinedUserDto = userRepository.save(user).toDto();

        joinedUserDto.setPassword("");

        return joinedUserDto;
    }

    @Override
    public UserDto login(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new RuntimeException("id not exist")
        );

        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("wrong password");
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
                .orElseThrow(() -> new RuntimeException("User not found")
                );

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("기존 비밀번호와 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void saveUsers(List<User> users){
        userRepository.saveAll(users);
    }

//    @Override
//    public WorkoutPlanDto addWorkoutPlan(List<WorkoutRoutineDto> workoutRoutineDtoList) {
//        // set루틴리스트 할건데 매개변수가 루틴Dto리스트라 엔터티로 변환 해줘서 담아야함.
//        // 하나씩 변환하고 루틴리스트에 add해서 -> set루틴리스트 하기
//        WorkoutPlanDto workoutPlanDto = new WorkoutPlanDto();
//        List<WorkoutRoutine> workoutRoutineList = new ArrayList<>();
//
//        for (WorkoutRoutineDto workoutRoutineDto : workoutRoutineDtoList) {
//            workoutRoutineRepository.save(workoutRoutineDto.toEntity(workoutRepository.findByWorkoutId(workoutRoutineDto.getWorkoutId()).toEntity())); // 루틴 DB에 저장 //
//            workoutRoutineList.add(workoutRoutineDto.toEntity(workoutRepository.findByWorkoutId(workoutRoutineDto.getWorkoutId()).toEntity())); // 값 찍어보기
//        }
//        workoutPlanDto.setWorkoutRoutineList(workoutRoutineList);
//        workoutPlanRepository.save(workoutPlanDto.toEntity(userRepository.findByUserId(workoutPlanDto.getUser_id()))); // 플랜 DB에 저장 //
//
//        return workoutPlanDto;
//    }
//}
}