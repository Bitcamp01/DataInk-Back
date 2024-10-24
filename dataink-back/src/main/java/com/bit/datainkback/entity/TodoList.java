package com.bit.datainkback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@SequenceGenerator(
        name = "todoListSeqGenerator",
        sequenceName = "TODOLIST_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoList {

    // 투두리스트 아이디
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "todoListSeqGenerator"
    )
    @Column(name = "todo_id")
    private Long todoId;

    // 유저 디테일하고 연결하는 게 맞는듯
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserDetail userDetail; // UserDetail 엔티티와의 관계 설정

    // 투두리스트 내용
    @Column(nullable = false)
    private String todoContent;

    // 투두리스트 생성 날짜
    @Column(nullable = false)
    private Timestamp createDate;

    // 완료 여부 필드
    @Column(nullable = false)
    private boolean isCompleted;

}
