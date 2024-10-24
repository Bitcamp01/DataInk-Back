package com.bit.datainkback.service;

import com.bit.datainkback.entity.TodoList;

import java.util.List;

public interface TodoListService {

    // 특정 유저의 투두리스트 가져오기(최신순으로)
    List<TodoList> getTodosByUserId(Long userId);

    // 투두리스트 저장
    TodoList saveTodo(TodoList todo);

    // 투두리스트 삭제
    void deleteTodoById(Long todoId);

    // 일정 날짜 이전의 완료된 투두리스트 삭제 (스케쥴링 적용 가능)
    void deleteCompletedTodos(Long userId);
}