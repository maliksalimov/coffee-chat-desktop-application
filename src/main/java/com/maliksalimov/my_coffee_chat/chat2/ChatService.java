package com.maliksalimov.my_coffee_chat.chat2;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public ChatMessage saveMessage(String sender, String message){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        return chatRepository.save(chatMessage);
    }

    public List<ChatMessage> getAllMessages(){
        return chatRepository.findAll();
    }

    public List<ChatMessage> getMessagesBySender(String sender){
        return chatRepository.findBySender(sender);
    }

}
