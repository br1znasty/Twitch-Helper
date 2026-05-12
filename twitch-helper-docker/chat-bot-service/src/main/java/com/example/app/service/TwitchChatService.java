package com.example.app.service;

import com.example.app.entity.ChatMessage;
import com.example.app.repository.ChatMessageRepository;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TwitchChatService {
    private static final Logger log = LoggerFactory.getLogger(TwitchChatService.class);

    private TwitchClient twitchClient;
    private final ChatMessageRepository messageRepository;
    private final CommandService commandService;

    @Value("${twitch.bot.username}")
    private String botUsername;

    @Value("${twitch.bot.oauth-token}")
    private String botOAuthToken;

    @Value("${twitch.channel.name}")
    private String channelName;

    public TwitchChatService(ChatMessageRepository messageRepository, CommandService commandService) {
        this.messageRepository = messageRepository;
        this.commandService = commandService;
    }

    @PostConstruct
    public void connect() {
        log.info("Connecting to Twitch chat as {}", botUsername);

        OAuth2Credential credential = new OAuth2Credential("twitch", botOAuthToken);

        twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(credential)
                .build();

        twitchClient.getChat().joinChannel(channelName);

        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, this::onMessage);

        log.info("Bot connected and listening to #{}", channelName);
    }

    private void onMessage(ChannelMessageEvent event) {
        String user = event.getUser().getName();
        String message = event.getMessage();
        String channel = normalizeChannel(event.getChannel().getName());

        log.info("[{}] {}: {}", channel, user, message);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannel(channel);
        chatMessage.setUsername(user);
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(Instant.now());
        messageRepository.save(chatMessage);

        if (message.startsWith("!")) {
            String response = commandService.handleCommand(user, message);
            if (response != null && !response.isEmpty()) {
                sendMessage(response);
            }
        }
    }

    public void sendMessage(String message) {
        if (twitchClient != null) {
            twitchClient.getChat().sendMessage(channelName, message);
            log.info("Bot sent: {}", message);
        }
    }

    public List<ChatMessage> getRecentMessages(String channel, int limit) {
        String targetChannel = normalizeChannel(channel == null || channel.isBlank() ? channelName : channel);
        int safeLimit = Math.max(1, Math.min(limit, 200));
        Instant since = Instant.now().minusSeconds(3600);
        return messageRepository.findRecentMessages(targetChannel, since, PageRequest.of(0, safeLimit));
    }

    public String getChannelName() {
        return channelName;
    }

    public boolean isConnected() {
        return twitchClient != null;
    }

    @PreDestroy
    public void disconnect() {
        if (twitchClient != null) {
            twitchClient.close();
            log.info("Bot disconnected");
        }
    }

    private String normalizeChannel(String channel) {
        if (channel == null) {
            return "";
        }
        String normalized = channel.trim().toLowerCase();
        return normalized.startsWith("#") ? normalized.substring(1) : normalized;
    }
}
