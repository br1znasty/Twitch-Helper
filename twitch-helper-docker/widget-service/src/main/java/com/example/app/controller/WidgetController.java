package com.example.controller;

import com.example.dto.CreateWidgetRequest;
import com.example.dto.UpdateWidgetRequest;
import com.example.dto.WidgetEventMessage;
import com.example.dto.WidgetEventRequest;
import com.example.dto.WidgetResponse;
import com.example.service.WidgetEventService;
import com.example.service.WidgetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/widgets")
@CrossOrigin(origins = "*")
public class WidgetController {
    private final WidgetService widgetService;
    private final WidgetEventService widgetEventService;

    public WidgetController(
            WidgetService widgetService,
            WidgetEventService widgetEventService
    ) {
        this.widgetService = widgetService;
        this.widgetEventService = widgetEventService;
    }

    @PostMapping
    public ResponseEntity<WidgetResponse> createWidget(
            @Valid @RequestBody CreateWidgetRequest request
    ) {
        WidgetResponse response = widgetService.createWidget(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WidgetResponse> getWidgetById(@PathVariable Long id) {
        WidgetResponse response = widgetService.getWidgetById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WidgetResponse>> getWidgetsByUserId(
            @RequestParam Long userId
    ) {
        List<WidgetResponse> widgets = widgetService.getWidgetsByUserId(userId);
        return ResponseEntity.ok(widgets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WidgetResponse> updateWidget(
            @PathVariable Long id,
            @RequestBody UpdateWidgetRequest request
    ) {
        WidgetResponse response = widgetService.updateWidget(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWidget(@PathVariable Long id) {
        widgetService.deleteWidget(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{publicToken}/test-event")
    public ResponseEntity<Void> sendTestEvent(
            @PathVariable String publicToken,
            @RequestBody WidgetEventRequest request
    ) {
        WidgetEventMessage message = new WidgetEventMessage(
                request.getType(),
                request.getPayload()
        );

        widgetEventService.sendEventToWidget(publicToken, message);

        return ResponseEntity.ok().build();
    }
}