package com.gen.ai.chatbot.controllers;

import com.gen.ai.chatbot.dto.chat.ChatRequestDto;
import com.gen.ai.chatbot.dto.chat.ChatResponseDto;
import com.gen.ai.chatbot.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/api/chats")
    public ChatResponseDto createChat(@RequestBody ChatRequestDto requestDto) {
        return chatService.createNewChat(requestDto);
    }

    @GetMapping("/api/chats")
    public List<ChatResponseDto> getAllChats() {
        return chatService.getAllChats();
    }

    @PatchMapping("/api/chats/{chatId}")
    public ChatResponseDto updateChat(@PathVariable Long chatId, @RequestBody ChatRequestDto requestDto) {
        return chatService.updateChat(chatId, requestDto);
    }

    @DeleteMapping("/api/chats/{chatId}")
    public void deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId);
    }
}
