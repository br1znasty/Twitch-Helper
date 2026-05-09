package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateWidgetRequest {
    private Boolean enabled;
    private String configJson;
}