package com.example.backend.message;

import com.example.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(Conversation conversation, User sender, User receiver, String content) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setRead(false);
        return messageRepository.save(message);
    }

    public List<Message> getMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId);
    }

    public void markAsRead(Message message) {
        message.setRead(true);
        messageRepository.save(message);
    }

    public List<Message> getUnreadMessages(User receiver) {
        return messageRepository.findByReceiverAndReadFalse(receiver);
    }

    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    public Optional<Message> getById(Long id) {
        return messageRepository.findById(id);
    }
} 