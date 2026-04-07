package com.gen.ai.chatbot.repository;

import com.gen.ai.chatbot.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
