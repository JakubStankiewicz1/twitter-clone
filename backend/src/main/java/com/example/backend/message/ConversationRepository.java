package com.example.backend.message;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByUser1AndUser2(User user1, User user2);
    List<Conversation> findByUser1OrUser2(User user1, User user2);

    @Query("SELECT c FROM Conversation c WHERE c.id = :conversationId AND (c.user1.id = :userId OR c.user2.id = :userId)")
    Optional<Conversation> findByIdAndParticipant(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
} 