package com.gen.ai.chatbot.services;

import com.gen.ai.chatbot.entity.Model;
import com.gen.ai.chatbot.dto.model.ModelResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModelService {

    public List<ModelResponseDto> getAllModels() {
        List<ModelResponseDto> models = new ArrayList<>();
        for (Model model: Model.values()) {
            models.add(new ModelResponseDto(model.getKey(), model.getName()));
        }
        return models;
    }
}
