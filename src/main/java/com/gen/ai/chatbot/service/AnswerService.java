package com.gen.ai.chatbot.service;

import com.gen.ai.chatbot.entity.Role;
import com.gen.ai.chatbot.entity.Chat;
import com.gen.ai.chatbot.entity.Message;
import com.gen.ai.chatbot.repository.ChatRepository;
import com.gen.ai.chatbot.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatModeService chatModeService;

    public Message sendAnswer(Chat chat, String question) {
        String answerString = chatModeService.process(question, chat.modeKey, chat.id);

        Message answer = new Message();
        answer.chat = chat;
        answer.role = Role.ASSISTANT;
        answer.text = answerString;

        return messageRepository.save(answer);
    }
}
