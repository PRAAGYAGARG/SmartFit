package com.fitness.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

// Creating UserResponse DTO to send safe data back to client

//@data means using Lombok to generate getters , setters for the private fields instead of manually writing them urself
@Data
public class UserResponse {
    private String id;     //can keep or remove user role, its up to u
    private String keycloakId;
    private String email;
    private String password; //usually not returned
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
