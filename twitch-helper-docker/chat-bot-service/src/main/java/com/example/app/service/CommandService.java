package com.example.app.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class CommandService {
    private final Map<String, Function<String, String>> commands = new HashMap<>();

    public CommandService() {
        commands.put("!ping", (user) -> "Pong! @ " + user);
        commands.put("!time", (user) -> "Сейчас " + java.time.LocalTime.now());
        commands.put("!help", (user) -> "Доступны: !ping, !time, !social");
        commands.put("!social", (user) -> "Твиттер: @example | Discord: gg/example");
    }

    public String handleCommand(String username, String message) {
        String[] parts = message.split(" ");
        String command = parts[0].toLowerCase();

        Function<String, String> handler = commands.get(command);
        if (handler != null) {
            return handler.apply(username);
        }

        return null; // команда не найдена
    }

    public void addCommand(String command, Function<String, String> handler) {
        commands.put(command.toLowerCase(), handler);
    }
}