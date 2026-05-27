package com.gen.ai.chatbot.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    public String title;
    public String modeKey;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    public List<Message> messages;
}
