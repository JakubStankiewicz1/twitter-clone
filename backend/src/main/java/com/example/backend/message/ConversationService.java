package com.example.backend.message;

import com.example.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;

    public Conversation getOrCreateConversation(User user1, User user2) {
        // Zapewnij, że user1.id < user2.id dla unikalności
        User first = user1;
        User second = user2;
        if (user1.getId() > user2.getId()) {
            first = user2;
            second = user1;
        }
        final User finalFirst = first;
        final User finalSecond = second;
        return conversationRepository.findByUser1AndUser2(finalFirst, finalSecond)
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setUser1(finalFirst);
                    c.setUser2(finalSecond);
                    return conversationRepository.save(c);
                });
    }

    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findByUser1OrUser2(user, user);
    }

    public Optional<Conversation> getById(Long id) {
        return conversationRepository.findById(id);
    }

    public Optional<Conversation> getByIdAndParticipant(Long conversationId, Long userId) {
        return conversationRepository.findByIdAndParticipant(conversationId, userId);
    }
} 