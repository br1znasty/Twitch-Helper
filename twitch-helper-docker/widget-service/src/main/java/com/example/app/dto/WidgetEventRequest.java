package com.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class WidgetEventRequest {
    private String type;
    private Map<String, Object> payload;
}