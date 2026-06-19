package com.gen.ai.chatbot.service;

import com.gen.ai.chatbot.dto.tool.WeatherResultResponseDto;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class WeatherToolService {

    private final WeatherApiService weatherApiService;

    public WeatherToolService(WeatherApiService weatherApiService) {
        this.weatherApiService = weatherApiService;
    }

    @Tool(description = "Get current weather for a city")
    public String getCurrentWeather(
            @ToolParam(description = "City name, for example Orlando, Salt Lake City")
            String city
    ) {
        WeatherResultResponseDto result = weatherApiService.getCurrentWeatherByCity(city);

        return """
                Weather in %s:
                Description: %s
                Temperature: %.1f C
                Feels like: %.1f C
                Humidity: %d%%
                """.formatted(
                result.city(),
                result.description(),
                result.temperature(),
                result.feelsLike(),
                result.humidity()
        );
    }
}
