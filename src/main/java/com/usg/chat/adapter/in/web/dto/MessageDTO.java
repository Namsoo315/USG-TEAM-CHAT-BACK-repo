package com.usg.chat.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MessageDTO {

    private String message;
    private String receiverEmail;
    private LocalDateTime timestamp;
    private Long chatRoomId;

    @Builder
    public MessageDTO (String message, String receiverEmail, LocalDateTime timestamp,Long chatRoomId){
        this.message = message;
        this.receiverEmail = receiverEmail;
        this.timestamp = timestamp;
        this.chatRoomId = chatRoomId;
    }
}
