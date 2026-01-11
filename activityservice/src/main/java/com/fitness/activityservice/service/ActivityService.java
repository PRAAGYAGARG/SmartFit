package com.fitness.activityservice.service;

import com.fitness.activityservice.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.stream.Collectors;

@Service      
@RequiredArgsConstructor
public class ActivityService { 

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String, Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    //User enters activity name, calories,duration and clicks "ADD ACTIVITY" button
    public ActivityResponse trackActivity(ActivityRequest request) {

        //created another service for userValidation and defined a method validateUser in it then called it to verify userID
        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        //if invalid throw exception
        if (!isValidUser) {
            throw new RuntimeException("Invalid User: " + request.getUserId());
        }

        //converts DTO → Entity
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        //Save activity in database
        Activity savedActivity = activityRepository.save(activity);

        //Publish event to Kafka (asynchronous)
        try {
            kafkaTemplate.send(topicName, savedActivity.getUserId(), savedActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        return mapToResponse(savedActivity);
    }

    //Converts a DB entity (Activity) into a response DTO (ActivityResponse).
    //created so that it can be used in the below functn ie getUserActivities 
    //Used in above functn also to return response(last line)
    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;

    }

    //Fetches all activities of a user and returns them as response DTO like we can see at bottom all activities are visible in our website.
    // with their name,duratn,calories
    //Used above functn (mapToResponse) inside it to fetch all activities as per DTO format as we don't want passwords, etc to be returred to user
    public List<ActivityResponse> getUserActivities(String userId) {
        List<Activity> activityList = activityRepository.findByUserId(userId);
        return activityList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

}
