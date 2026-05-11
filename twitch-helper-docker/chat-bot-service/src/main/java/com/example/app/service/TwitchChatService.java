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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TwitchChatService {
    private static final Logger log = LoggerFactory.getLogger(TwitchChatService.class);

    private TwitchClient twitchClient;
    private final ChatMessageRepository messageRepository;
    private final CommandService commandService;

    private final ConcurrentHashMap<String, List<ChatMessage>> messageCache = new ConcurrentHashMap<>();

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
        String channel = event.getChannel().getName();

        log.info("[{}] {}: {}", channel, user, message);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannel(channel);
        chatMessage.setUsername(user);
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(Instant.now());
        messageRepository.save(chatMessage);

        messageCache.remove(channel);

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
        String cacheKey = channel + ":" + limit;
        if (messageCache.containsKey(cacheKey)) {
            return messageCache.get(cacheKey);
        }

        Instant since = Instant.now().minusSeconds(3600);
        List<ChatMessage> messages = messageRepository
                .findTopByChannelAndTimestampAfterOrderByTimestampDesc(channel, since)
                .stream()
                .limit(limit)
                .toList();

        messageCache.put(cacheKey, messages);
        return messages;
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
}