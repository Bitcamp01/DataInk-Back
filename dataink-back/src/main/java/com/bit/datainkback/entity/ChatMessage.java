package com.bit.datainkback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@SequenceGenerator(
        name = "chatGenerator",
        sequenceName = "CHAT_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "chatGenerator"
    )
    private Long id;

    private String senderId;
    private String receiverId;
    private String content;
    private Long roomId;
    private LocalDateTime timestamp;
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

}

