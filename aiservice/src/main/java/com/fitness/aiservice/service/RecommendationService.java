package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.respository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//Provides APIs to fetch stored recommendations.
//Below we have mentioned 2 API's ( ie 2 endpoints )

@Service

// Lombok generates a constructor for all final fields.
// Spring uses this constructor to inject the RecommendationRepository bean.
@RequiredArgsConstructor

public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    
    //Fetches all AI recommendations for a given user.
    public List<Recommendation> getUserRecommendation(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    //Fetches the AI recommendation for a specific activity.
    public Recommendation getActivityRecommendation(String activityId) {
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(() -> new RuntimeException("No recommendation found for this activity: " + activityId));
    }
}
