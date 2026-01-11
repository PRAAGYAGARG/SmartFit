package com.fitness.userservice.services;

import com.fitness.userservice.UserRepository;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service       //helps identify UserService as a service to the springFramework, and creates a bean of this service on startup
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repository; //created before writing register() logic as first need a way to talk to the database.


    //Method to register the user
    public UserResponse register(RegisterRequest request) { 
        
        //if already exists then we return the existing user details / could have thrown error also that already user exists so can't create new user 
        if (repository.existsByEmail(request.getEmail())) {    //created this existsByEmail method in UserRepository as don't want service to directly interact with db
            User existingUser = repository.findByEmail(request.getEmail()); //findbyEmail also in UserRepo. as again interactn with db all through UserRepo
            UserResponse userResponse = new UserResponse();  //functn return type is userResponse so now creating object for that annd equating it to the existing user details
            userResponse.setId(existingUser.getId());
            userResponse.setPassword(existingUser.getPassword()); 
            userResponse.setEmail(existingUser.getEmail());
            userResponse.setFirstName(existingUser.getFirstName());
            userResponse.setLastName(existingUser.getLastName());
            userResponse.setCreatedAt(existingUser.getCreatedAt());
            userResponse.setUpdatedAt(existingUser.getUpdatedAt());
            return userResponse;
        }
        
        //Creating new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setKeycloakId(request.getKeycloakId());
        user.setLastName(request.getLastName());
        user.setPassword(request.getPassword());
        //saving the new user in repository ie the db
        User savedUser = repository.save(user);
        // Creating UserResponse DTO to send safe data back to client
        UserResponse userResponse = new UserResponse();
        userResponse.setId(savedUser.getId());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setKeycloakId(savedUser.getKeycloakId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());
        return userResponse;
    }

    //method to fetch user Profile
    public UserResponse getUserProfile(String userId) {
        User user = repository.findById(userId)  // if user found then save in user var. else throw error that no user exists 
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setPassword(user.getPassword());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;

    }

    public Boolean existByUserId(String userId) {
        log.info("Calling User Service for {}", userId);
        return repository.existsByKeycloakId(userId);
    }
}
