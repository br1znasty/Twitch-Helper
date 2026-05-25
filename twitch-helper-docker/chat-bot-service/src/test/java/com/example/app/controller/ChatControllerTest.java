package com.example.app.controller;

import com.example.app.dto.ChatMessageDto;
import com.example.app.entity.ChatMessage;
import com.example.app.service.TwitchChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private TwitchChatService chatService;

    @InjectMocks
    private ChatController chatController;

    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        chatMessage = new ChatMessage();
        chatMessage.setChannel("testchannel");
        chatMessage.setUsername("someuser");
        chatMessage.setMessage("Hello world");
        chatMessage.setTimestamp(Instant.now());
    }

    // --- getMessages ---

    @Test
    void getMessages_returns200WithDtoList() {
        when(chatService.getRecentMessages(eq("testchannel"), eq(50)))
                .thenReturn(List.of(chatMessage));

        ResponseEntity<List<ChatMessageDto>> response =
                chatController.getMessages("testchannel", 50);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("someuser", response.getBody().get(0).getUsername());
        assertEquals("Hello world", response.getBody().get(0).getMessage());
    }

    @Test
    void getMessages_returnsEmptyListWhenNoMessages() {
        when(chatService.getRecentMessages(any(), anyInt())).thenReturn(List.of());

        ResponseEntity<List<ChatMessageDto>> response =
                chatController.getMessages(null, 50);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getMessages_clampsLimitTo200() {
        when(chatService.getRecentMessages(any(), eq(200))).thenReturn(List.of());

        chatController.getMessages("testchannel", 999);

        // Контроллер обрезает limit до 200 через Math.min
        verify(chatService).getRecentMessages(eq("testchannel"), eq(200));
    }

    // --- sendMessage ---

    @Test
    void sendMessage_returns200() {
        SendMessageRequest request = new SendMessageRequest();
        request.setMessage("Hello from bot!");

        ResponseEntity<Void> response = chatController.sendMessage(request);

        assertEquals(200, response.getStatusCode().value());
        verify(chatService).sendMessage("Hello from bot!");
    }

    // --- getStatus ---

    @Test
    void getStatus_returnsConnectedAndChannel() {
        when(chatService.isConnected()).thenReturn(true);
        when(chatService.getChannelName()).thenReturn("testchannel");

        ResponseEntity<Map<String, Object>> response = chatController.getStatus();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getBody().get("connected"));
        assertEquals("testchannel", response.getBody().get("channel"));
    }

    @Test
    void getStatus_returnsDisconnectedWhenBotNotStarted() {
        when(chatService.isConnected()).thenReturn(false);
        when(chatService.getChannelName()).thenReturn("testchannel");

        ResponseEntity<Map<String, Object>> response = chatController.getStatus();

        assertEquals(false, response.getBody().get("connected"));
    }
}
