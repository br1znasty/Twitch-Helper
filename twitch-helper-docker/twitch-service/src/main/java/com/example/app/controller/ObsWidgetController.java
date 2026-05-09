package com.example.app.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Controller
public class ObsWidgetController {
    private static final String OBS_WIDGET_HTML_PATH = "static/obs-widget/obs-widget-html.html";

    private static final Set<String> ALLOWED_WIDGET_TYPES = Set.of(
            "viewers",
            "status",
            "game",
            "title",
            "stream-time",
            "followers",
            "language",
            "mature",
            "stream-type",
            "display-name",
            "description",
            "summary"
    );

    @GetMapping(value = "/widget", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> widget() {
        return getWidgetHtml();
    }

    @GetMapping(value = "/widget/{type}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> widgetByType(@PathVariable String type) {
        if (!ALLOWED_WIDGET_TYPES.contains(type)) {
            return ResponseEntity.notFound().build();
        }

        return getWidgetHtml();
    }

    private ResponseEntity<String> getWidgetHtml() {
        try {
            ClassPathResource resource = new ClassPathResource(OBS_WIDGET_HTML_PATH);

            String html = StreamUtils.copyToString(
                    resource.getInputStream(),
                    StandardCharsets.UTF_8
            );

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.TEXT_HTML)
                    .cacheControl(CacheControl.noStore())
                    .body(html);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load OBS widget HTML", e);
        }
    }
}
