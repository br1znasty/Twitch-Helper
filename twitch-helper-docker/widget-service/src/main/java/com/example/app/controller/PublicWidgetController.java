package com.example.controller;

import com.example.dto.WidgetResponse;
import com.example.service.WidgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/widgets")
@CrossOrigin(origins = "*")
public class PublicWidgetController {
    private final WidgetService widgetService;

    public PublicWidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @GetMapping("/{publicToken}")
    public ResponseEntity<WidgetResponse> getWidgetByPublicToken(
            @PathVariable String publicToken
    ) {
        WidgetResponse response = widgetService.getWidgetByPublicToken(publicToken);
        return ResponseEntity.ok(response);
    }
}