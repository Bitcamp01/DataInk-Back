package com.bit.datainkback.controller;

import com.bit.datainkback.dto.TodoListDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.TodoList;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
import com.bit.datainkback.repository.UserDetailRepository;
import com.bit.datainkback.service.TodoListService;
import com.bit.datainkback.service.UserService;
import com.bit.datainkback.service.impl.CustomUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/TodoList")
@CrossOrigin(origins = "http://localhost:3000")
public class TodoListController {
    @Autowired
    private TodoListService todoListService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailRepository userDetailRepository;

    // 특정 유저의 투두리스트 중 todoContent 검색
    @GetMapping("/todoContent")
    public List<TodoListDto> getTodoContents(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();

        return todoListService.getTodosByUserId(userId).stream()
                .map(todo -> new TodoListDto(
                        todo.getTodoId(), // todoId
                        todo.getUserDetail().getUserId(), // userId
                        todo.getTodoContent(), // todoContent
                        todo.getCreateDate(), // createdDate
                        todo.isCompleted() // isCompleted
                ))
                .collect(Collectors.toList());
    }

    // 새로운 투두리스트 생성
    @PostMapping("/todoCreate")
    public ResponseEntity<TodoList> createTodo(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody TodoListDto todoDto) {
        // userDetails에서 userId를 가져옴
        Long userId = userDetails.getUser().getUserId();

        // userId를 통해 User를 가져옴
        User user = userService.getUserById(userId);

        // User를 통해 UserDetail을 가져옴
        UserDetail userDetail = userDetailRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("UserDetail not found for user: " + user.getUserId()));

        // TodoList 생성
        TodoList todo = TodoList.builder()
                .userDetail(userDetail) // UserDetail 설정
                .todoContent(todoDto.getTodoContent())
                .createDate(new Timestamp(System.currentTimeMillis())) // 현재 시간 설정
                .isCompleted(todoDto.isCompleted())
                .build();

        // TodoList 저장
        TodoList savedTodo = todoListService.saveTodo(todo);

        // 생성된 투두 리스트 객체를 클라이언트로 반환
        return ResponseEntity.ok(savedTodo);
    }


    // 투두리스트 삭제
    @DeleteMapping("/todoDelete")
    public ResponseEntity<String> deleteTodo(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long todoId) {
        // userDetails에서 userId를 가져옴
        Long userId = userDetails.getUser().getUserId();

        // 해당 투두를 찾음
        TodoList todo = (TodoList) todoListService.getTodosByUserId(todoId);

        // 투두가 현재 사용자 소유인지 확인
        if (!todo.getUserDetail().getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이 투두를 삭제할 권한이 없습니다.");
        }

        // 투두 삭제
        todoListService.deleteTodoById(todoId);
        return ResponseEntity.ok("투두가 성공적으로 삭제되었습니다.");
    }


}
