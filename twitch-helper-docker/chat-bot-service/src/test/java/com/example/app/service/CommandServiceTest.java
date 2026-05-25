package com.example.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandServiceTest {

    private CommandService commandService;

    @BeforeEach
    void setUp() {
        commandService = new CommandService();
    }

    @Test
    void handleCommand_ping_returnsPong() {
        String result = commandService.handleCommand("kirill", "!ping");
        assertNotNull(result);
        assertTrue(result.contains("Pong!"));
        assertTrue(result.contains("kirill"));
    }

    @Test
    void handleCommand_time_returnsCurrentTime() {
        String result = commandService.handleCommand("kirill", "!time");
        assertNotNull(result);
        assertTrue(result.contains("Сейчас"));
    }

    @Test
    void handleCommand_help_listsCommands() {
        String result = commandService.handleCommand("kirill", "!help");
        assertNotNull(result);
        assertTrue(result.contains("!ping"));
        assertTrue(result.contains("!time"));
        assertTrue(result.contains("!social"));
    }

    @Test
    void handleCommand_social_returnsSocialLinks() {
        String result = commandService.handleCommand("kirill", "!social");
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    void handleCommand_unknown_returnsNull() {
        String result = commandService.handleCommand("kirill", "!unknown");
        assertNull(result);
    }

    @Test
    void handleCommand_isCaseInsensitive() {
        String lower = commandService.handleCommand("kirill", "!ping");
        String upper = commandService.handleCommand("kirill", "!PING");
        assertEquals(lower, upper);
    }

    @Test
    void handleCommand_withTrailingArgs_stillWorks() {
        String result = commandService.handleCommand("kirill", "!ping some extra args");
        assertNotNull(result);
        assertTrue(result.contains("Pong!"));
    }

    @Test
    void addCommand_andCallIt() {
        commandService.addCommand("!hello", (user) -> "Hello, " + user + "!");
        String result = commandService.handleCommand("kirill", "!hello");
        assertEquals("Hello, kirill!", result);
    }
}
