package com.gen.ai.chatbot.dto.tool;

public record GeoResponseDto(
        String name,
        double lat,
        double lon,
        String country,
        String state
) {
}
