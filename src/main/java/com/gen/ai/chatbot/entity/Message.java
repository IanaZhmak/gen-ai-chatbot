package com.gen.ai.chatbot.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    public String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    public Chat chat;

    @Enumerated(EnumType.STRING)
    public Role role;

    @CreatedDate
    public Instant createdAt;

    public String filePath;
    public String fileName;
}
