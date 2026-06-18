package com.gen.ai.chatbot.service;

import com.gen.ai.chatbot.entity.Mode;
import org.springframework.stereotype.Service;

@Service
public class ChatModeService {

    private final SimpleChatService simpleChatService;
    private final PromptBasedChatService promptBasedChatService;
    private final RagChatService ragChatService;
    private final ToolsChatService toolsChatService;
    private final MemoryChatService memoryChatService;

    public ChatModeService(SimpleChatService simpleChatService, PromptBasedChatService promptBasedChatService,
                           RagChatService ragChatService, ToolsChatService toolsChatService, MemoryChatService memoryChatService) {
        this.simpleChatService = simpleChatService;
        this.promptBasedChatService = promptBasedChatService;
        this.ragChatService = ragChatService;
        this.toolsChatService = toolsChatService;
        this.memoryChatService = memoryChatService;
    }

    public String process(String question, String chatMode, Long chatId) {
        Mode mode = Mode.fromKey(chatMode);

        return switch (mode) {
            case SIMPLECHAT -> simpleChatService.ask(question);
            case PROMPTBASEDCHAT -> promptBasedChatService.ask(question);
            case RAGCHAT -> ragChatService.ask(question, chatId);
            case TOOLSCHAT -> toolsChatService.ask(question);
            case MEMORYCHAT -> memoryChatService.ask(question, chatId);
        };
    }
}
