package com.gen.ai.chatbot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class MemoryChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public MemoryChatService(ChatClient chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    public String ask(String question, Long conversationId) {
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(String.valueOf(conversationId))
                .build();

        return chatClient.prompt()
                .system("""
                        You are a helpful assistant.
                        Use previous conversation context when it is relevant.
                        If you do not know, say so.
                        """)
                .user(question)
                .advisors(memoryAdvisor)
                .call()
                .content();
    }
}
