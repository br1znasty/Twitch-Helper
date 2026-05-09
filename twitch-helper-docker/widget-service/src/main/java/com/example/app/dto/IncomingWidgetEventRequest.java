package com.example.dto;

import com.example.entity.WidgetType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class IncomingWidgetEventRequest {
    private Long userId;
    private WidgetType widgetType;
    private String eventType;
    private Map<String, Object> payload;
}