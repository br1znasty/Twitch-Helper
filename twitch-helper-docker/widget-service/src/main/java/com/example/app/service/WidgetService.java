package com.example.service;

import com.example.dto.CreateWidgetRequest;
import com.example.dto.UpdateWidgetRequest;
import com.example.dto.WidgetResponse;
import com.example.entity.Widget;
import com.example.repository.WidgetRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class WidgetService {
    private final WidgetRepository widgetRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public WidgetService(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public WidgetResponse createWidget(CreateWidgetRequest request) {
        Widget widget = new Widget();

        widget.setUserId(request.getUserId());
        widget.setType(request.getType());
        widget.setPublicToken(generatePublicToken());
        widget.setEnabled(true);

        if (request.getConfigJson() == null || request.getConfigJson().isBlank()) {
            widget.setConfigJson("{}");
        } else {
            widget.setConfigJson(request.getConfigJson());
        }

        Widget saved = widgetRepository.save(widget);

        return toResponse(saved);
    }

    public WidgetResponse getWidgetById(Long id) {
        Widget widget = widgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Widget not found"));

        return toResponse(widget);
    }

    public WidgetResponse getWidgetByPublicToken(String publicToken) {
        Widget widget = widgetRepository.findByPublicToken(publicToken)
                .orElseThrow(() -> new RuntimeException("Widget not found"));

        return toResponse(widget);
    }

    public List<WidgetResponse> getWidgetsByUserId(Long userId) {
        return widgetRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WidgetResponse updateWidget(Long id, UpdateWidgetRequest request) {
        Widget widget = widgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Widget not found"));

        if (request.getEnabled() != null) {
            widget.setEnabled(request.getEnabled());
        }

        if (request.getConfigJson() != null && !request.getConfigJson().isBlank()) {
            widget.setConfigJson(request.getConfigJson());
        }

        Widget saved = widgetRepository.save(widget);

        return toResponse(saved);
    }

    public void deleteWidget(Long id) {
        if (!widgetRepository.existsById(id)) {
            throw new RuntimeException("Widget not found");
        }

        widgetRepository.deleteById(id);
    }

    private String generatePublicToken() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private WidgetResponse toResponse(Widget widget) {
        return new WidgetResponse(
                widget.getId(),
                widget.getUserId(),
                widget.getType(),
                widget.getPublicToken(),
                widget.isEnabled(),
                widget.getConfigJson(),
                widget.getCreatedAt(),
                widget.getUpdatedAt()
        );
    }
}