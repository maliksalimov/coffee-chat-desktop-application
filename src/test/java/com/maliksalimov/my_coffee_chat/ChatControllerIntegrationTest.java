package com.maliksalimov.my_coffee_chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maliksalimov.my_coffee_chat.chat2.ChatMessage;
import com.maliksalimov.my_coffee_chat.chat2.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ChatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatService chatService;

    private ChatMessage buildMessage(String sender, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        return chatMessage;
    }

    @Test
    void post_send_should_return_200_with_saved_message() throws Exception {
        ChatMessage saved = buildMessage("Alice", "I'd like to order a latte");
        when(chatService.saveMessage("Alice", "I'd like to order a latte")).thenReturn(saved);

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("sender", "Alice", "message", "I'd like to order a latte"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sender").value("Alice"))
                .andExpect(jsonPath("$.message").value("I'd like to order a latte"));
    }

    @Test
    void post_send_should_return_message_with_correct_sender() throws Exception {
        ChatMessage saved = buildMessage("Bob", "Hello");
        when(chatService.saveMessage("Bob", "Hello")).thenReturn(saved);

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("sender", "Bob", "message", "Hello"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sender").value("Bob"));
    }

    @Test
    void get_all_messages_should_return_200_with_message_list() throws Exception {
        when(chatService.getAllMessages()).thenReturn(List.of(
                buildMessage("Alice", "order espresso"),
                buildMessage("Bob", "order cappuccino")
        ));

        mockMvc.perform(get("/api/chat/messages")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sender").value("Alice"))
                .andExpect(jsonPath("$[1].sender").value("Bob"));
    }

    @Test
    void get_all_messages_should_return_empty_list_when_no_messages_exist() throws Exception {
        when(chatService.getAllMessages()).thenReturn(List.of());

        mockMvc.perform(get("/api/chat/messages")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void get_messages_by_sender_should_return_200_with_filtered_messages() throws Exception {
        when(chatService.getMessagesBySender("Alice")).thenReturn(List.of(
                buildMessage("Alice", "order latte"),
                buildMessage("Alice", "order mocha")
        ));

        mockMvc.perform(get("/api/chat/messages/Alice")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sender").value("Alice"))
                .andExpect(jsonPath("$[0].message").value("order latte"))
                .andExpect(jsonPath("$[1].message").value("order mocha"));
    }

    @Test
    void get_messages_by_sender_should_return_empty_list_for_unknown_sender() throws Exception {
        when(chatService.getMessagesBySender(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/chat/messages/unknown")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
