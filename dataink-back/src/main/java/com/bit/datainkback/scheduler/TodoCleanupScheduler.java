package com.bit.datainkback.scheduler;

import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.TodoListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TodoCleanupScheduler {

    @Autowired
    private TodoListService todoListService;
    @Autowired
    private UserRepository userRepository;

    // 매일 자정에 완료된 투두리스트 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteCompletedTodosForAllUsers() {
        // 1. 모든 유저의 ID 가져오기
        List<Long> allUserIds = userRepository.findAllUserIds();

        // 2. 각 유저의 완료된 투두 항목 삭제
        for (Long userId : allUserIds) {
            // 완료된 투두 항목 삭제
            todoListService.deleteCompletedTodos(userId);
        }
    }
}
