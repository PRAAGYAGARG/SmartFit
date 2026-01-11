package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//Generates AI-based fitness recommendations from activity data.
//Builds prompt for AI model
//Calls GeminiService
//Parses AI response
//Converts it into Recommendation entity
//Handles fallback logic if AI fails

@Service
@Slf4j
@AllArgsConstructor
public class ActivityAIService {

    // Service responsible for communicating with Google Gemini API
    // This service sends prompts to Gemini and receives AI-generated responses
    private final GeminiService geminiService;

    // Main method that is called when an activity event is consumed from Kafka
    // It generates AI recommendations for a given activity
    public Recommendation generateRecommendation(Activity activity) {

        // Create a detailed prompt using activity data
        String prompt = createPromptForActivity(activity);

        // Call Gemini API using GeminiService and get raw AI response (JSON as String)
        String aiResponse = geminiService.getRecommendations(prompt);

        // Log the raw AI response for debugging and visibility
        log.info("RESPONSE FROM AI {} ", aiResponse);

        // Process the AI response and convert it into Recommendation entity
        return processAIResponse(activity, aiResponse);
    }

    // Processes the raw AI response string and converts it into Recommendation object
    private Recommendation processAIResponse(Activity activity, String aiResponse) {
        try { 
            // Jackson ObjectMapper used to parse JSON responses
            ObjectMapper mapper = new ObjectMapper();

            // Parse the full Gemini API response JSON
            JsonNode rootNode = mapper.readTree(aiResponse);

            // Navigate through Gemini response structure to extract the text output
            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .get("parts")
                    .get(0)
                    .path("text");

            // Gemini returns JSON wrapped inside markdown ```json blocks
            // Remove those wrappers to extract clean JSON content
            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();

            // Parse the cleaned JSON content returned by Gemini
            JsonNode analysisJson = mapper.readTree(jsonContent);

            // Extract the "analysis" section from the AI response
            JsonNode analysisNode = analysisJson.path("analysis");

            // Build a single formatted string containing all analysis sections
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories:");

            // Extract structured lists from AI response
            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            // Build and return Recommendation entity to be saved in DB
            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getType().toString())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            // If AI response parsing fails, return a default recommendation
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }
    }

    // Fallback recommendation when AI response fails or is invalid
    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType().toString())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness consultant"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Extracts safety guidelines from AI response JSON
    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> safety.add(item.asText()));
        }
        return safety.isEmpty() ?
                Collections.singletonList("Follow general safety guidelines") :
                safety;
    }

    // Extracts workout suggestions from AI response JSON
    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });
        }
        return suggestions.isEmpty() ?
                Collections.singletonList("No specific suggestions provided") :
                suggestions;
    }

    // Extracts improvement areas and recommendations from AI response JSON
    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if (improvementsNode.isArray()) {
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, detail));
            });
        }
        return improvements.isEmpty() ?
                Collections.singletonList("No specific improvements provided") :
                improvements;
    }

    // Appends a specific analysis section (overall, pace, etc.) to the full analysis text
    private void addAnalysisSection(StringBuilder fullAnalysis,
                                    JsonNode analysisNode,
                                    String key,
                                    String prefix) {

        if (!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    // Creates a structured prompt that is sent to Gemini AI
    // Forces Gemini to return output in a strict JSON format
    private String createPromptForActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
