package com.bit.datainkback.repository;

import com.bit.datainkback.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
    // 유저 ID에 따른 투두리스트 가져오기(최신순으로 정렬)
    List<TodoList> findByUserDetailUserUserIdOrderByCreateDateDesc(Long userId);

    // 해당 유저의 완료된 투두 항목 조회
    List<TodoList> findByUserDetailUserUserIdAndIsCompletedTrue(Long userId);
}
