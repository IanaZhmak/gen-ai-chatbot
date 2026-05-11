package com.gen.ai.chatbot.controller;

import com.gen.ai.chatbot.dto.mode.ModeResponseDto;
import com.gen.ai.chatbot.service.ModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ModeController {

    @Autowired
    private ModeService modeService;

    @GetMapping("/api/models/chat")
    public List<ModeResponseDto> getAllModes() {
        return modeService.getAllModes();
    }
}
