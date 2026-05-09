package com.example.service;

import com.example.dto.IncomingWidgetEventRequest;
import com.example.dto.WidgetEventMessage;
import com.example.entity.Widget;
import com.example.repository.WidgetRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WidgetEventService {
    private final WidgetRepository widgetRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WidgetEventService(
            WidgetRepository widgetRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.widgetRepository = widgetRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void processIncomingEvent(IncomingWidgetEventRequest request) {
        if (request.getUserId() == null) {
            throw new RuntimeException("User id is required");
        }

        if (request.getWidgetType() == null) {
            throw new RuntimeException("Widget type is required");
        }

        if (request.getEventType() == null || request.getEventType().isBlank()) {
            throw new RuntimeException("Event type is required");
        }

        List<Widget> widgets = widgetRepository.findByUserIdAndTypeAndEnabledTrue(
                request.getUserId(),
                request.getWidgetType()
        );

        WidgetEventMessage message = new WidgetEventMessage(
                request.getEventType(),
                request.getPayload()
        );

        for (Widget widget : widgets) {
            sendEventToWidget(widget.getPublicToken(), message);
        }
    }

    public void sendEventToWidget(String publicToken, WidgetEventMessage message) {
        String destination = "/topic/widgets/" + publicToken;

        messagingTemplate.convertAndSend(destination, message);
    }
}