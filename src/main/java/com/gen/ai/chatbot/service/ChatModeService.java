package com.gen.ai.chatbot.service;

import com.gen.ai.chatbot.entity.Mode;
import com.gen.ai.chatbot.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatModeService {

    @Autowired
    private SimpleChatService simpleChatService;

    @Autowired
    private PromptBasedChatService promptBasedChatService;

    @Autowired
    private RagChatService ragChatService;

    @Autowired
    private ToolsChatService toolsChatService;

    @Autowired
    private MemoryChatService memoryChatService;

    @Autowired
    private ChatRepository chatRepository;

    public String process(String question, String chatMode, Long chatId) {
        Mode mode = Mode.fromKey(chatMode);

        return switch (mode) {
            case SIMPLECHAT -> simpleChatService.ask(question);
            case PROMPTBASEDCHAT -> promptBasedChatService.ask(question);
            case RAGCHAT -> ragChatService.ask(question);
            case TOOLSCHAT -> toolsChatService.ask(question);
            case MEMORYCHAT -> memoryChatService.ask(question, chatId);
        };
    }
}
