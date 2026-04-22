package com.gen.ai.chatbot.services;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileUploadService {

    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if(!file.isEmpty()) {
            try {
                Path path = Paths.get("C:/uploads/", System.currentTimeMillis() + "_" + file.getOriginalFilename());
                file.transferTo(path.toFile());

                return path.toString();
            } catch (IOException e) {
                return "Upload is failed: " + e.getMessage();
            }
        } else {
            return "Upload is failed because file was empty";
        }
    }


}
