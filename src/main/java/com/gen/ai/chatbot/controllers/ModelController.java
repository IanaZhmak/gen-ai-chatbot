package com.gen.ai.chatbot.controllers;

import com.gen.ai.chatbot.dto.model.ModelResponseDto;
import com.gen.ai.chatbot.services.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ModelController {

    @Autowired
    private ModelService modelService;

    @GetMapping("/api/models/chat")
    public List<ModelResponseDto> getAllModels() {
        return modelService.getAllModels();
    }
}
