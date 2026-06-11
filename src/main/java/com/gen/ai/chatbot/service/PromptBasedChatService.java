package com.gen.ai.chatbot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromptBasedChatService {

    @Autowired
    private ChatClient chatClient;

    public String ask(String question) {
        return chatClient.prompt()
                .system("""
                        You are helpful assistant.
                        Answer briefly and clearly.
                        If you do not know answer, say so.
                        """)
                .user(question)
                .call()
                .content();
    }
}
