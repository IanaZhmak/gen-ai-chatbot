package com.gen.ai.chatbot.dto.tool;

import java.util.List;

public record CurrentWeatherResponseDto(
        String name,
        Main main,
        List<Weather> weather
) {
    public record Main(
            double temp,
            double feels_like,
            int humidity
    ) {}
    public record Weather(
            String main,
            String description
    ) {}
}
