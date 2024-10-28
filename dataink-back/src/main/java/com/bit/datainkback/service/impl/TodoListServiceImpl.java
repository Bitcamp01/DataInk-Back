package com.bit.datainkback.service.impl;

import com.bit.datainkback.entity.TodoList;
import com.bit.datainkback.repository.TodoListRepository;
import com.bit.datainkback.service.TodoListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoListServiceImpl implements TodoListService {

    @Autowired
    private TodoListRepository todoListRepository;

    @Override
    public List<TodoList> getTodosByUserId(Long userId) {
        // userId로 투두 리스트를 가져오고 createDate 기준으로 오래된 순서로 정렬
        return todoListRepository.findByUserDetailUserUserIdOrderByCreateDateDesc(userId)
                .stream()
                .sorted(Comparator.comparing(TodoList::getCreateDate)) // 오래된 순서로 정렬
                .collect(Collectors.toList());
    }

    @Override
    public TodoList getTodoById(Long todoId) {
        return todoListRepository.findById(todoId).orElse(null);
    }

    @Override
    public TodoList saveTodo(TodoList todo) {
        todo.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
        return todoListRepository.save(todo);
    }

    @Override
    public void deleteTodoById(Long todoId) {
        todoListRepository.deleteById(todoId);
    }

    @Override
    public void deleteCompletedTodos(Long userId) {
        List<TodoList> completedTodos = todoListRepository.findByUserDetailUserUserIdAndIsCompletedTrue(userId);
        todoListRepository.deleteAll(completedTodos);
    }
}