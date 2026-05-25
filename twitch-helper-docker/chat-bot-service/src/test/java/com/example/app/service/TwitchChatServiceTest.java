package com.example.app.service;

import com.example.app.entity.ChatMessage;
import com.example.app.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitchChatServiceTest {

    @Mock
    private ChatMessageRepository messageRepository;

    @Mock
    private CommandService commandService;

    private TwitchChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new TwitchChatService(messageRepository, commandService);
        // Инжектируем @Value-поля вручную, без Spring-контекста
        ReflectionTestUtils.setField(chatService, "botUsername", "TestBot");
        ReflectionTestUtils.setField(chatService, "botOAuthToken", "oauth:testtoken");
        ReflectionTestUtils.setField(chatService, "channelName", "testchannel");
    }

    // --- isConnected ---

    @Test
    void isConnected_returnsFalseBeforeConnect() {
        // connect() не вызывался — twitchClient == null
        assertFalse(chatService.isConnected());
    }

    // --- getChannelName ---

    @Test
    void getChannelName_returnsInjectedValue() {
        assertEquals("testchannel", chatService.getChannelName());
    }

    // --- getRecentMessages ---

    @Test
    void getRecentMessages_usesDefaultChannelWhenNullPassed() {
        when(messageRepository.findRecentMessages(eq("testchannel"), any(Instant.class), any()))
                .thenReturn(List.of());

        chatService.getRecentMessages(null, 10);

        verify(messageRepository).findRecentMessages(eq("testchannel"), any(Instant.class), any());
    }

    @Test
    void getRecentMessages_usesDefaultChannelWhenBlankPassed() {
        when(messageRepository.findRecentMessages(eq("testchannel"), any(Instant.class), any()))
                .thenReturn(List.of());

        chatService.getRecentMessages("   ", 10);

        verify(messageRepository).findRecentMessages(eq("testchannel"), any(Instant.class), any());
    }

    @Test
    void getRecentMessages_normalizesChannelWithHash() {
        when(messageRepository.findRecentMessages(eq("mychannel"), any(Instant.class), any()))
                .thenReturn(List.of());

        chatService.getRecentMessages("#mychannel", 10);

        // "#mychannel" должен превратиться в "mychannel"
        verify(messageRepository).findRecentMessages(eq("mychannel"), any(Instant.class), any());
    }

    @Test
    void getRecentMessages_normalizesChannelToLowercase() {
        when(messageRepository.findRecentMessages(eq("mychannel"), any(Instant.class), any()))
                .thenReturn(List.of());

        chatService.getRecentMessages("MyChannel", 10);

        verify(messageRepository).findRecentMessages(eq("mychannel"), any(Instant.class), any());
    }

    @Test
    void getRecentMessages_clampsLimitTo200() {
        when(messageRepository.findRecentMessages(any(), any(Instant.class), any()))
                .thenReturn(List.of());

        chatService.getRecentMessages("testchannel", 999);

        // PageRequest должен быть с size=200, не 999
        ArgumentCaptor<org.springframework.data.domain.Pageable> pageableCaptor =
                ArgumentCaptor.forClass(org.springframework.data.domain.Pageable.class);
        verify(messageRepository).findRecentMessages(any(), any(Instant.class), pageableCaptor.capture());
        assertEquals(200, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getRecentMessages_clampsLimitToMinimum1() {
        when(messageRepository.findRecentMessages(any(), any(Instant.class), any()))
                .thenReturn(List.of());

        chatService.getRecentMessages("testchannel", 0);

        ArgumentCaptor<org.springframework.data.domain.Pageable> pageableCaptor =
                ArgumentCaptor.forClass(org.springframework.data.domain.Pageable.class);
        verify(messageRepository).findRecentMessages(any(), any(Instant.class), pageableCaptor.capture());
        assertEquals(1, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getRecentMessages_returnsMessagesFromRepository() {
        ChatMessage msg = new ChatMessage();
        msg.setChannel("testchannel");
        msg.setUsername("someuser");
        msg.setMessage("hello");
        msg.setTimestamp(Instant.now());

        when(messageRepository.findRecentMessages(any(), any(Instant.class), any()))
                .thenReturn(List.of(msg));

        List<ChatMessage> result = chatService.getRecentMessages("testchannel", 10);

        assertEquals(1, result.size());
        assertEquals("someuser", result.get(0).getUsername());
        assertEquals("hello", result.get(0).getMessage());
    }
}
