package com.gen.ai.chatbot.entity;

public enum Mode {
    SIMPLECHAT("simplechat", "Simple Chat"),
    PROMPTBASEDCHAT("promptbasedchat", "Prompt-Based Chat"),
    RAGCHAT("ragchat", "RAG Chat"),
    TOOLSCHAT("toolschat", "Tools Chat"),
    MEMORYCHAT("memorychat", "Memory Chat");

    private final String key;
    private final String name;

    Mode(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static Mode fromKey(String key) {
        for (Mode mode: Mode.values()) {
            if (mode.getKey().equals(key)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown mode key: " + key);
    }
}
