package com.gen.ai.chatbot.controller;

import com.gen.ai.chatbot.dto.message.MessageRequestDto;
import com.gen.ai.chatbot.dto.message.MessageResponseDto;
import com.gen.ai.chatbot.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageResponseDto> createMessage(@RequestParam(value = "question", required = false) MessageRequestDto messageRequest,
                                                            @PathVariable Long chatId,
                                                            @RequestParam(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.sendMessage(messageRequest, chatId, file));
    }

    @GetMapping("/{chatId}/messages")
    public List<MessageResponseDto> getAllMessages(@PathVariable Long chatId) {
        return messageService.getAllMessages(chatId);
    }
}
