package com.example.dto;

import com.example.entity.WidgetType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateWidgetRequest {
    @NotNull
    private Long userId;
    @NotNull
    private WidgetType type;
    private String configJson;
}