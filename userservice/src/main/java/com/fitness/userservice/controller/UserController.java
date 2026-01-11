package com.fitness.userservice.controller;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")

@AllArgsConstructor
// Lombok generates a constructor ONLY for the fields declared in that class ie here UserService class coz mentioned private UserService userService; below
// Spring uses this constructor to inject the UserService bean ie we don't have to manually  create constructors and objects now
//Now u don't have to manually create constructor , etc so work is simplified.

public class UserController {
    private UserService userService;  //dependency injection ie the bean created of UserService is called here

    //endpt to fetch user profile , here also return type UserResponse as now we will use it as standard way of replying 
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId)); //implemented getUserProfile method in UserService
    }

    @PostMapping("/register")    // First user will register
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        //returning UserResponse(dto) so that confidential things does not reach the user 
        //Mostly everything we will return using UserResponse only everytime coz it is the safe response we have made
        return ResponseEntity.ok(userService.register(request));
    }

    //endpt through which activityService treats UserService as API so that it can verify ki userID same or not
    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.existByUserId(userId));
    }
}
 