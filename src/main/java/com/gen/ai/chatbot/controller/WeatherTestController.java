package com.gen.ai.chatbot.controller;

import com.gen.ai.chatbot.dto.tool.WeatherResultResponseDto;
import com.gen.ai.chatbot.service.WeatherApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class WeatherTestController {

    private final WeatherApiService weatherApiService;

    public WeatherTestController(WeatherApiService weatherApiService) {
        this.weatherApiService = weatherApiService;
    }

    @GetMapping("/weather")
    public WeatherResultResponseDto testWeather(@RequestParam String city) {
        return weatherApiService.getCurrentWeatherByCity(city);
    }
}
