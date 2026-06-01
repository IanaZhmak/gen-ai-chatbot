package com.gen.ai.chatbot.service;

import com.gen.ai.chatbot.entity.Mode;
import com.gen.ai.chatbot.dto.chat.ChatRequestDto;
import com.gen.ai.chatbot.dto.chat.ChatResponseDto;
import com.gen.ai.chatbot.entity.Chat;
import com.gen.ai.chatbot.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public ChatResponseDto createNewChat(@RequestBody ChatRequestDto requestDto) {
        Mode mode = Mode.fromKey(requestDto.modeKey());
        Chat chat = new Chat();
        chat.title = requestDto.title();
        chat.modeKey = mode.getKey();
        Chat savedChat = chatRepository.save(chat);
        return new ChatResponseDto(savedChat.id, savedChat.title, savedChat.modeKey);
    }

    public List<ChatResponseDto> getAllChats() {
        List<Chat> chats = chatRepository.findAll();
        List<ChatResponseDto> chatResponse = new ArrayList<>();
        for (Chat chat: chats) {
            chatResponse.add(new ChatResponseDto(chat.id, chat.title, chat.modeKey));
        }
        return chatResponse;
    }

    public ChatResponseDto updateChat(@PathVariable Long chatId, @RequestBody ChatRequestDto requestDto) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();

        if (requestDto.title() != null) {
            chat.title = requestDto.title();
        }
        if (requestDto.modeKey() != null) {
            chat.modeKey = requestDto.modeKey();
        }

        Chat updatedChat = chatRepository.save(chat);
        return new ChatResponseDto(updatedChat.id, updatedChat.title, updatedChat.modeKey);
    }

    public void deleteChat(@PathVariable Long chatId) {
        chatRepository.delete(chatRepository.findById(chatId).orElseThrow());
    }

}
