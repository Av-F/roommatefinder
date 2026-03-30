package com.mates.roommatefinder.dto;

import java.time.LocalDateTime;

import com.mates.roommatefinder.model.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String message;
    private LocalDateTime timestamp;

    // Mapper method
    public static ChatMessageDTO fromChatMessage(ChatMessage chatMessage) {
        return ChatMessageDTO.builder()
                .senderId(chatMessage.getSender().getId())
                .senderName(chatMessage.getSender().getProfile().getName())
                .receiverId(chatMessage.getReceiver().getId())
                .receiverName(chatMessage.getReceiver().getProfile().getName())
                .message(chatMessage.getMessage())
                .timestamp(chatMessage.getTimestamp())
                .build();
    }
}