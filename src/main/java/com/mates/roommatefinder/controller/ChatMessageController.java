package com.mates.roommatefinder.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mates.roommatefinder.dto.ChatMessageDTO;
import com.mates.roommatefinder.model.ChatMessage;
import com.mates.roommatefinder.security.SecurityUtils;
import com.mates.roommatefinder.service.ChatMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * Send a message as the authenticated user
     */
    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(
                                   @RequestParam Long receiverId,
                                   @RequestParam String message) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ChatMessage chatMessage = chatMessageService.sendMessage(currentUserId, receiverId, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(chatMessage);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid message request from user {}: {}", currentUserId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Failed to send message from {} to {}: {}", currentUserId, receiverId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Send a message (deprecated - use POST /api/chat/send with query params)
     */
    @PostMapping("/send/{senderId}/{receiverId}")
    public ResponseEntity<ChatMessage> sendMessageDeprecated(
                                   @PathVariable Long senderId,
                                   @PathVariable Long receiverId,
                                   @RequestParam String message) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Authorization: user can only send messages as themselves
        if (!currentUserId.equals(senderId)) {
            log.warn("Unauthorized message: User {} attempted to send message as user {}", currentUserId, senderId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            ChatMessage chatMessage = chatMessageService.sendMessage(senderId, receiverId, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(chatMessage);
        } catch (Exception e) {
            log.error("Failed to send message from {} to {}: {}", senderId, receiverId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get conversation with another user
     * User must be one of the participants in the conversation
     */
    @GetMapping("/{otherUserId}")
    public ResponseEntity<List<ChatMessageDTO>> getConversation(@PathVariable Long otherUserId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<ChatMessageDTO> conversation = chatMessageService.getConversation(currentUserId, otherUserId);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            log.error("Failed to get conversation for user {} with user {}: {}", 
                     currentUserId, otherUserId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get conversation between two users (deprecated - use GET /api/chat/{userId})
     * Authorization: user must be one of the participants
     */
    @GetMapping("/{user1}/{user2}")
    public ResponseEntity<List<ChatMessageDTO>> getConversationDeprecated(@PathVariable Long user1,
                                            @PathVariable Long user2) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Authorization: user can only view conversations they are part of
        if (!currentUserId.equals(user1) && !currentUserId.equals(user2)) {
            log.warn("Unauthorized conversation access: User {} attempted to access conversation between {} and {}", 
                     currentUserId, user1, user2);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            List<ChatMessageDTO> conversation = chatMessageService.getConversation(user1, user2);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            log.error("Failed to get conversation between {} and {}: {}", user1, user2, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}