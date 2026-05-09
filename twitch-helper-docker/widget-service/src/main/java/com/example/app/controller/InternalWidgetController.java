package com.example.controller;

import com.example.dto.IncomingWidgetEventRequest;
import com.example.service.WidgetEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/widgets")
public class InternalWidgetController {
    private final WidgetEventService widgetEventService;

    public InternalWidgetController(WidgetEventService widgetEventService) {
        this.widgetEventService = widgetEventService;
    }

    @PostMapping("/events")
    public ResponseEntity<Void> receiveWidgetEvent(
            @RequestBody IncomingWidgetEventRequest request
    ) {
        widgetEventService.processIncomingEvent(request);
        return ResponseEntity.ok().build();
    }
}