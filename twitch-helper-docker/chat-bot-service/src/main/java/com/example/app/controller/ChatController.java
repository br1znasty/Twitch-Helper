package com.example.app.controller;

import com.example.app.dto.ChatMessageDto;
import com.example.app.entity.ChatMessage;
import com.example.app.service.TwitchChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class ChatController {

    private final TwitchChatService chatService;

    public ChatController(TwitchChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageDto>> getMessages(
            @RequestParam String channel,
            @RequestParam(defaultValue = "50") int limit
    ) {
        List<ChatMessage> messages = chatService.getRecentMessages(channel, Math.min(limit, 200));
        List<ChatMessageDto> dtos = messages.stream()
                .map(m -> new ChatMessageDto(m.getUsername(), m.getMessage(), m.getTimestamp()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody SendMessageRequest request) {
        chatService.sendMessage(request.getMessage());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("connected", chatService.isConnected());
        status.put("channel", chatService.getChannelName());
        return ResponseEntity.ok(status);
    }
}