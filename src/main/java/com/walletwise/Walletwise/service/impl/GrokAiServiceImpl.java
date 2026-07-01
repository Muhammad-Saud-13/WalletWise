package com.walletwise.Walletwise.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletwise.Walletwise.service.GrokAiService;
import com.walletwise.Walletwise.exception.QuotaExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class GrokAiServiceImpl implements GrokAiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String model;

    public GrokAiServiceImpl(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${groq.api.url}") String apiUrl) {

        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateContent(String prompt) {
        // Construct standard OpenAI / Groq structured JSON body
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            String response = webClient.post()
                    .uri("")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isBlank()) {
                throw new RuntimeException("Groq returned an empty response.");
            }

            return extractGeneratedText(response);

        } catch (WebClientResponseException e) {
            // Map 429 errors seamlessly so your heuristic fallbacks continue to work
            if (e.getStatusCode().value() == 429) {
                throw new QuotaExceededException("Groq free tier limit hit: " + e.getResponseBodyAsString(), e);
            }
            throw new RuntimeException("Groq API error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AI content using Groq: " + e.getMessage(), e);
        }
    }

    private String extractGeneratedText(String responseJson) throws Exception {
        JsonNode rootNode = objectMapper.readTree(responseJson);
        JsonNode choices = rootNode.path("choices");

        if (!choices.isArray() || choices.isEmpty()) {
            throw new RuntimeException("Groq response payload missing choices array");
        }

        JsonNode contentNode = choices.get(0).path("message").path("content");
        if (contentNode.isMissingNode() || contentNode.asText().isBlank()) {
            throw new RuntimeException("Groq response missing text content inside message");
        }

        return contentNode.asText().trim();
    }
}