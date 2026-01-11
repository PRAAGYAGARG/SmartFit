package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.respository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

//Acts as a Kafka consumer
//Listens to activity events from Kafka and triggers AI processing.

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    // Service responsible for generating AI-based recommendations
    private final ActivityAIService activityAIService;

    // Repository used to store generated recommendations in the AI service database
    private final RecommendationRepository recommendationRepository;

    // Kafka listener that listens to activity events published by ActivityService
    // - topics is read from configuration
    // - groupId ensures this consumer belongs to a consumer group
    @KafkaListener(topics = "${kafka.topic.name}", groupId = "activity-processor-group")

    // AiService receives activity data from kafka here as functn parameter
    public void processActivity(Activity activity) {

        log.info("Received Activity for processing: {}", activity.getUserId());

        // giving activity data to gemini to generate AI recommendation 
        Recommendation recommendation =
                activityAIService.generateRecommendation(activity);

        // Save the generated recommendation in the AI service database
        recommendationRepository.save(recommendation);
    }
}

