package com.fitness.aiservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

//Handles communication with Google Gemini AI API.

@Service
public class GeminiService {

    // WebClient used to make HTTP calls to Google Gemini API
    private final WebClient webClient;

    // Gemini API endpoint URL (loaded from application.yml / application.properties)
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    // Gemini API key for authentication (kept in config, not hardcoded)
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // Constructor injection of WebClient.Builder
    // Spring provides the builder bean and we build a WebClient from it
    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // This method sends activity details to Gemini AI and returns raw AI response
    public String getRecommendations(String details) {

        //we saw in Postman ki after sending POST request the response was of this form
        // so creating structure accordingly so that we can get the required details from the res.
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", details)
                        })
                }
        );

        // Send POST request to Gemini API
        String response = webClient.post()
                // Gemini endpoint
                .uri(geminiApiUrl)

                // Required headers
                .header("Content-Type","application/json")
                .header("X-goog-api-key", geminiApiKey)

                // Attach request body
                .bodyValue(requestBody)

                // Execute request and retrieve response
                .retrieve()

                // Convert response body to String
                .bodyToMono(String.class)

                // Block current thread until response is received
                // (acceptable here because this runs asynchronously via Kafka)
                .block();

        // Return raw JSON response from Gemini AI
        return response;
    }
}
