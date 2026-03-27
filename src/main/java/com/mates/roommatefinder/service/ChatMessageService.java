package com.mates.roommatefinder.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.mates.roommatefinder.dto.ChatMessageDTO;
import com.mates.roommatefinder.model.ChatMessage;
import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.ChatMessageRepository;
import com.mates.roommatefinder.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    // Send a message
    public ChatMessage sendMessage(Long senderId, Long receiverId, String message) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        return chatMessageRepository.save(chatMessage);
    }

    // Get conversation between two users
    public List<ChatMessageDTO> getConversation(Long userId1, Long userId2) {
    User user1 = userRepository.findById(userId1)
            .orElseThrow(() -> new RuntimeException("User not found"));
    User user2 = userRepository.findById(userId2)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return chatMessageRepository
            .findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(user1, user2, user1, user2)
            .stream()
            .map(ChatMessageDTO::fromChatMessage)
            .collect(Collectors.toList());
    }
}