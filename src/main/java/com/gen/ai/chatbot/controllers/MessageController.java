package com.gen.ai.chatbot.controllers;

import com.gen.ai.chatbot.dto.message.MessageRequestDto;
import com.gen.ai.chatbot.dto.message.MessageResponseDto;
import com.gen.ai.chatbot.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/api/chats/{chatId}/messages")
    public MessageResponseDto createMessage(@RequestParam(value = "question", required = false) MessageRequestDto messageRequest, @PathVariable Long chatId, @RequestParam(value = "file", required = false) MultipartFile file) {
        return messageService.sendMessage(messageRequest, chatId, file);
    }

    @GetMapping("/api/chats/{chatId}/messages")
    public List<MessageResponseDto> getAllMessages(@PathVariable Long chatId) {
        return messageService.getAllMessages(chatId);
    }
}
