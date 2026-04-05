package com.gen.ai.chatbot.entity;

public enum Model {
    CODEMODE("codemode", "Code Mode"),
    OLLAMA("ollama", "Ollama"),
    EMBEDDING("embedding", "Embedding");

    private final String key;
    private final String name;

    Model(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static Model fromKey(String key) {
        for (Model model: Model.values()) {
            if (model.getKey().equals(key)) {
                return model;
            }
        }
        throw new IllegalArgumentException("Unknown model key: " + key);
    }
}
