package com.gen.ai.chatbot.exception;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(Long chatId) {
        super("Chat not found: " + chatId);
    }
}
