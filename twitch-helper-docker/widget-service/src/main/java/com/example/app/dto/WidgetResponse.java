package com.example.dto;

import com.example.entity.WidgetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class WidgetResponse {
    private final Long id;
    private final Long userId;
    private final WidgetType type;
    private final String publicToken;
    private final boolean enabled;
    private final String configJson;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}