package com.myxapp.backend.service;

import com.myxapp.backend.model.Message;
import com.myxapp.backend.model.User;
import com.myxapp.backend.repository.MessageRepository;
import com.myxapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Message> getConversation(Long user1, Long user2) {
        return messageRepository.findConversation(user1, user2);
    }

    public List<Message> getAllUserMessages(Long userId) {
        return messageRepository.findAllByUser(userId);
    }

    public Message sendMessage(Long senderId, Long recipientId, String content) {
        User sender = userRepository.findById(senderId).orElseThrow();
        User recipient = userRepository.findById(recipientId).orElseThrow();
        Message msg = new Message();
        msg.setSender(sender);
        msg.setRecipient(recipient);
        msg.setContent(content);
        return messageRepository.save(msg);
    }
} 