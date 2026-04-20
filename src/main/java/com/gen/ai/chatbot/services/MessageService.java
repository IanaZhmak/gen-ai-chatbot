package com.gen.ai.chatbot.services;

import com.gen.ai.chatbot.entity.Role;
import com.gen.ai.chatbot.dto.message.MessageRequestDto;
import com.gen.ai.chatbot.dto.message.MessageResponseDto;
import com.gen.ai.chatbot.entity.Chat;
import com.gen.ai.chatbot.entity.Message;
import com.gen.ai.chatbot.repository.ChatRepository;
import com.gen.ai.chatbot.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private FileUploadService fileUploadService;

    public MessageResponseDto sendMessage(@RequestParam(value = "question", required = false) MessageRequestDto messageRequest, @PathVariable Long chatId, @RequestParam(value = "file", required = false) MultipartFile file) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();

        Message question = new Message();
        question.text = messageRequest.question();
        question.chat = chat;
        question.role = Role.USER;

        if (file != null && !file.isEmpty()) {
            question.filePath = fileUploadService.uploadFile(file);
            question.fileName = file.getOriginalFilename();
        }

        if (!chat.modelKey.equals("embedding")) {
            messageRepository.save(question);
            Message generatedAnswer = answerService.sendAnswer(chat);
            return new MessageResponseDto(generatedAnswer.role.name(), generatedAnswer.text, "");
        }

        Message lastMessage = messageRepository.findByChatIdLastMessage(chatId);
        messageRepository.save(question);
        if (lastMessage != null && lastMessage.role == Role.USER) {
            Message generatedAnswerForEmbeddingModel = answerService.sendAnswer(chat);
            return new MessageResponseDto(generatedAnswerForEmbeddingModel.role.name(), generatedAnswerForEmbeddingModel.text, "");
        }

        return null;

//        if (chat.modelKey.equals("embedding")) {
//            Message lastMessage = messageRepository.findByChatIdLastMessage(chatId);
//
//                if (lastMessage == null || lastMessage.role == Role.ASSISTANT) {
//                    Message question = new Message();
//                    question.text = messageRequest.question();
//                    question.chat = chat;
//                    question.role = Role.USER;
//                    messageRepository.save(question);
//                } else {
//                    Message question = new Message();
//                    question.text = messageRequest.question();
//                    question.chat = chat;
//                    question.role = Role.USER;
//                    messageRepository.save(question);
//                    Message generatedAnswerForEmbeddingModel = answerService.sendAnswer(chat);
//                    return new MessageResponseDto(generatedAnswerForEmbeddingModel.role.name(), generatedAnswerForEmbeddingModel.text, "");
//                }
//
//
//        } else {
//            Message question = new Message();
//            question.text = messageRequest.question();
//            question.chat = chat;
//            question.role = Role.USER;
//
//            if (file != null && !file.isEmpty()) {
//                question.filePath = fileUploadService.uploadFile(file);
//                question.fileName = file.getOriginalFilename();
//            }
//            messageRepository.save(question);
//        }
//
//        Message generatedAnswer = answerService.sendAnswer(chat);
//        return new MessageResponseDto(generatedAnswer.role.name(), generatedAnswer.text, "");
    }

    public List<MessageResponseDto> getAllMessages(@PathVariable Long chatId) {
        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
        List<MessageResponseDto> response = new ArrayList<>();

        for (Message message: messages) {
            response.add(new MessageResponseDto(message.role.name(), message.text, message.fileName));
        }
        return response;
    }
}
