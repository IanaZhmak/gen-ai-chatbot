package com.gen.ai.chatbot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ToolsChatService {

    private final ChatClient chatClient;
    private final WeatherToolService weatherToolService;

    public ToolsChatService(ChatClient chatClient, WeatherToolService weatherToolService) {
        this.chatClient = chatClient;
        this.weatherToolService = weatherToolService;
    }

    public String ask(String question) {
        return chatClient.prompt()
                .system("""
                        You are a helpful assistant.
                        For any weather question, you must use the weather tool.
                        Do not answer from your own knowledge.
                        If the user asks about weather in any city, call the tool first.
                        """)
                .user(question)
                .tools(weatherToolService)
                .call()
                .content();
    }
}
