package com.example.backend.message;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);
    List<Message> findByReceiverAndReadFalse(User receiver);
} 