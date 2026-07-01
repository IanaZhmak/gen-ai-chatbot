package com.gen.ai.chatbot.service;

import com.gen.ai.chatbot.exception.RateLimitException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    @Value("${app.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${app.rate-limit.capacity:10}")
    private int capacity;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, Duration.ofHours(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public void checkLimit(String ipAddress) {
        if (!rateLimitEnabled) {
            return;
        }
        Bucket bucket = buckets.computeIfAbsent(ipAddress, key -> createBucket());

        if (!bucket.tryConsume(1)) {
            throw new RateLimitException("Too many requests. Please try again later.");
        }
    }
}
