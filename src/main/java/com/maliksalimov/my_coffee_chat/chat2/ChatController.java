package com.maliksalimov.my_coffee_chat.chat2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> send(@RequestBody Map<String, String> body) {
        ChatMessage saved = chatService.saveMessage(body.get("sender"), body.get("message"));
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> getAllMessages() {
        return ResponseEntity.ok(chatService.getAllMessages());
    }

    @GetMapping("/messages/{sender}")
    public ResponseEntity<List<ChatMessage>> getMessagesBySender(@PathVariable String sender) {
        return ResponseEntity.ok(chatService.getMessagesBySender(sender));
    }
}
