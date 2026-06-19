package com.gen.ai.chatbot.repository;

import com.gen.ai.chatbot.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatIdOrderByCreatedAtAsc(Long chatId);

    @Query(value = "SELECT * FROM message WHERE chat_id = :chatId ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Message findByChatIdLastMessage(@Param("chatId") Long chatId);
}
