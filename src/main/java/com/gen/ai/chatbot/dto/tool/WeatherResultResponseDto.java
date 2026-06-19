package com.gen.ai.chatbot.dto.tool;

public record WeatherResultResponseDto(
        String city,
        String description,
        double temperature,
        double feelsLike,
        int humidity
) {
}
