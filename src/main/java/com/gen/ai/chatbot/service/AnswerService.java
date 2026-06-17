package com.gen.ai.chatbot.service;

import com.gen.ai.chatbot.entity.Role;
import com.gen.ai.chatbot.entity.Chat;
import com.gen.ai.chatbot.entity.Message;
import com.gen.ai.chatbot.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    private final MessageRepository messageRepository;
    private final ChatModeService chatModeService;

    public AnswerService(MessageRepository messageRepository, ChatModeService chatModeService) {
        this.messageRepository = messageRepository;
        this.chatModeService = chatModeService;
    }

    public Message sendAnswer(Chat chat, String question) {
        String answerString = chatModeService.process(question, chat.modeKey, chat.id);

        Message answer = new Message();
        answer.chat = chat;
        answer.role = Role.ASSISTANT;
        answer.text = answerString;

        return messageRepository.save(answer);
    }
}
