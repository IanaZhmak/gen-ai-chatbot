package com.gen.ai.chatbot.service;

import com.gen.ai.chatbot.entity.Role;
import com.gen.ai.chatbot.dto.message.MessageRequestDto;
import com.gen.ai.chatbot.dto.message.MessageResponseDto;
import com.gen.ai.chatbot.entity.Chat;
import com.gen.ai.chatbot.entity.Message;
import com.gen.ai.chatbot.exception.ChatNotFoundException;
import com.gen.ai.chatbot.repository.ChatRepository;
import com.gen.ai.chatbot.repository.MessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final AnswerService answerService;
    private final RagChatService ragChatService;

    private static final int MAX_MESSAGE_LENGTH = 2000;

    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository,
                          AnswerService answerService, RagChatService ragChatService) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.answerService = answerService;
        this.ragChatService = ragChatService;
    }

    public MessageResponseDto sendMessage(MessageRequestDto messageRequest, Long chatId, MultipartFile file) {
        validateMessageLength(messageRequest);

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException(chatId));

        Message question = new Message();
        question.text = messageRequest.question();
        question.chat = chat;
        question.role = Role.USER;

        if (file != null && !file.isEmpty()) {
            question.fileName = file.getOriginalFilename();
            ragChatService.processFile(file, chat.id);
        }

        messageRepository.save(question);

        Message generatedAnswer = answerService.sendAnswer(chat, messageRequest.question());
        return new MessageResponseDto(generatedAnswer.role.name(), generatedAnswer.text, "");
    }

    public List<MessageResponseDto> getAllMessages(Long chatId) {
        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
        List<MessageResponseDto> response = new ArrayList<>();

        for (Message message: messages) {
            response.add(new MessageResponseDto(message.role.name(), message.text, message.fileName));
        }
        return response;
    }

    private void validateMessageLength(MessageRequestDto messageRequest) {
        if (messageRequest == null || messageRequest.question() == null) {
            return;
        }

        if (messageRequest.question().length() > MAX_MESSAGE_LENGTH) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message is too long for demo mode. Maximum length is 2000 characters."
            );
        }
    }
}
