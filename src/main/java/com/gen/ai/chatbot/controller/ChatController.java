package com.gen.ai.chatbot.controller;

import com.gen.ai.chatbot.dto.chat.ChatRequestDto;
import com.gen.ai.chatbot.dto.chat.ChatResponseDto;
import com.gen.ai.chatbot.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponseDto> createChat(@RequestBody ChatRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.createNewChat(requestDto));
    }

    @GetMapping
    public List<ChatResponseDto> getAllChats() {
        return chatService.getAllChats();
    }

    @PatchMapping("/{chatId}")
    public ChatResponseDto updateChat(@PathVariable Long chatId, @RequestBody ChatRequestDto requestDto) {
        return chatService.updateChat(chatId, requestDto);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.noContent().build();
    }
}
