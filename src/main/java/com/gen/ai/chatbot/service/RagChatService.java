package com.gen.ai.chatbot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class RagChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagChatService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public void processFile(MultipartFile file, Long chatId) {
        validate(file);

        try {
            Resource resource = new InputStreamResource(file.getInputStream());
            TikaDocumentReader reader = new TikaDocumentReader(resource);
            List<Document> documents = reader.get();

            for (Document document: documents) {
                document.getMetadata().put("chatId", chatId);
                document.getMetadata().put("fileName", file.getOriginalFilename());
            }

            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(500)
                    .build();
            List<Document> chunks = splitter.apply(documents);

            vectorStore.add(chunks);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }

    public String ask(String question, Long chatId) {
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(5)
                .similarityThreshold(0.3)
                .filterExpression("chatId == " + chatId)
                .build();

        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(request)
                .build();

        return chatClient.prompt()
                .system("""
                        You are helpful assistant.
                        Answer based on provided documents.
                        If you do not know answer, say so.
                        """)
                .user(question)
                .advisors(advisor)
                .call()
                .content();
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
    }
}
