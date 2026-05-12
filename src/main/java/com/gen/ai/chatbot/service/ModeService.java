package com.gen.ai.chatbot.service;

import com.gen.ai.chatbot.entity.Mode;
import com.gen.ai.chatbot.dto.mode.ModeResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModeService {

    public List<ModeResponseDto> getAllModes() {
        List<ModeResponseDto> modes = new ArrayList<>();
        for (Mode mode: Mode.values()) {
            modes.add(new ModeResponseDto(mode.getKey(), mode.getName()));
        }
        return modes;
    }
}
