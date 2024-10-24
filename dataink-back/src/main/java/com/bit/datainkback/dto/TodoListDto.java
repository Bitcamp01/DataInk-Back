package com.bit.datainkback.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
public class TodoListDto {

    // 두투리스트 아이디
    private Long todoId;

    // 유저 아이디
    private Long userId;

    // 투두리스트 내용
    private String todoContent;

    // 투두리스트 생성 시간
    private Timestamp createdDate;

    // 투두리스트 완료 여부
    private boolean isCompleted;

    public TodoListDto(Long todoId, Long userId, String todoContent, Timestamp createdDate, boolean isCompleted) {
        this.todoId = todoId;
        this.userId = userId;
        this.todoContent = todoContent;
        this.createdDate = createdDate;
        this.isCompleted = isCompleted;
    }
}
