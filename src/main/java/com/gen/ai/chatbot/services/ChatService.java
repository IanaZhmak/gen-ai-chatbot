package com.gen.ai.chatbot.services;

import com.gen.ai.chatbot.entity.Model;
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
        Model model = Model.fromKey(requestDto.modelKey());
        Chat chat = new Chat();
        chat.title = requestDto.title();
        chat.modelKey = model.getKey();
        Chat savedChat = chatRepository.save(chat);
        return new ChatResponseDto(savedChat.id, savedChat.title, savedChat.modelKey);
    }

    public List<ChatResponseDto> getAllChats() {
        List<Chat> chats = chatRepository.findAll();
        List<ChatResponseDto> chatResponse = new ArrayList<>();
        for (Chat chat: chats) {
            chatResponse.add(new ChatResponseDto(chat.id, chat.title, chat.modelKey));
        }
        return chatResponse;
    }

    public ChatResponseDto updateChat(@PathVariable Long chatId, @RequestBody ChatRequestDto requestDto) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();

        if (requestDto.title() != null) {
            chat.title = requestDto.title();
        }
        if (requestDto.modelKey() != null) {
            chat.modelKey = requestDto.modelKey();
        }

        Chat updatedChat = chatRepository.save(chat);
        return new ChatResponseDto(updatedChat.id, updatedChat.title, updatedChat.modelKey);
    }

    public void deleteChat(@PathVariable Long chatId) {
        chatRepository.delete(chatRepository.findById(chatId).orElseThrow());
    }

}
