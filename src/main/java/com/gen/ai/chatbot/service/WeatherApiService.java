package com.gen.ai.chatbot.service;

import com.gen.ai.chatbot.dto.tool.CurrentWeatherResponseDto;
import com.gen.ai.chatbot.dto.tool.GeoResponseDto;
import com.gen.ai.chatbot.dto.tool.WeatherResultResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class WeatherApiService {

    private final RestClient restClient;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.geo.url.path}")
    private String geoUrlPath;

    @Value("${weather.current.url.path}")
    private String currentUrlPath;

    public WeatherApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    public WeatherResultResponseDto getCurrentWeatherByCity(String city) {
        List<GeoResponseDto> geoResults = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(geoUrlPath)
                        .queryParam("q", city)
                        .queryParam("limit", 1)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (geoResults == null || geoResults.isEmpty()) {
            throw new RuntimeException("City not found: " + city);
        }

        GeoResponseDto geo = geoResults.getFirst();

        CurrentWeatherResponseDto weatherResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(currentUrlPath)
                        .queryParam("lat", geo.lat())
                        .queryParam("lon", geo.lon())
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .body(CurrentWeatherResponseDto.class);

        if (weatherResponse == null || weatherResponse.main() == null
                || weatherResponse.weather() == null || weatherResponse.weather().isEmpty()) {
            throw new RuntimeException("Weather data not available for city: " + city);
        }

        return new WeatherResultResponseDto(
                weatherResponse.name(),
                weatherResponse.weather().getFirst().description(),
                weatherResponse.main().temp(),
                weatherResponse.main().feels_like(),
                weatherResponse.main().humidity()
        );
    }
}
