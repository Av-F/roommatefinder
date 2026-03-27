package com.mates.roommatefinder.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.mates.roommatefinder.dto.ChatMessageDTO;
import com.mates.roommatefinder.model.ChatMessage;
import com.mates.roommatefinder.service.ChatMessageService;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    // Send a message
    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestParam Long senderId,
                                   @RequestParam Long receiverId,
                                   @RequestParam String message) {
        return chatMessageService.sendMessage(senderId, receiverId, message);
    }

    // Get conversation between two users
    @GetMapping("/{user1}/{user2}")
    public List<ChatMessageDTO> getConversation(@PathVariable Long user1,
                                            @PathVariable Long user2) {
    return chatMessageService.getConversation(user1, user2);
    }
}