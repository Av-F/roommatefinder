package com.mates.roommatefinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mates.roommatefinder.model.ChatMessage;
import com.mates.roommatefinder.model.User;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
        User sender, User receiver, User receiver2, User sender2
    );
}