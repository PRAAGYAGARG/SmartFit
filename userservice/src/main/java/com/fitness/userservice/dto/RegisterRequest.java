package com.fitness.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

//this dto is for form data that user sends to the server

//@data means using Lombok to generate getters , setters for the private fields instead of manually writing them urself
@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    private String keycloakId;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must have atleast 6 characters")
    private String password;
    private String firstName;
    private String lastName;
}
 