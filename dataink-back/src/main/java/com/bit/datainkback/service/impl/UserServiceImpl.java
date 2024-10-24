package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.enums.AuthenType;
import com.bit.datainkback.jwt.JwtProvider;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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
        userDto.setAuthen(AuthenType.ROLE_USER);
        userDto.setRegdate(new Timestamp(System.currentTimeMillis()));
        userDto.setStatus("active");

        UserDto joinedUserDto = userRepository.save(userDto.toEntity()).toDto();

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