package com.maliksalimov.my_coffee_chat;

import com.maliksalimov.my_coffee_chat.chat2.ChatMessage;
import com.maliksalimov.my_coffee_chat.chat2.ChatRepository;
import com.maliksalimov.my_coffee_chat.chat2.ChatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void save_message_should_call_repository_save_once() {
        when(chatRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());

        chatService.saveMessage("Alice", "Hello");

        verify(chatRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    void get_all_messages_should_call_repository_find_all_once() {
        when(chatRepository.findAll()).thenReturn(List.of());

        chatService.getAllMessages();

        verify(chatRepository, times(1)).findAll();
    }

    @Test
    void get_messages_by_sender_should_call_repository_with_correct_argument() {
        String sender = "Alice";
        when(chatRepository.findBySender(sender)).thenReturn(List.of());

        chatService.getMessagesBySender(sender);

        verify(chatRepository, times(1)).findBySender(sender);
    }
}
