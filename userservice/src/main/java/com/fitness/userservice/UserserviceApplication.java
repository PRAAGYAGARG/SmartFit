package com.fitness.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}

}

//It is the entry point of the User microservice , Just like server.js in MERN.
//Starts the application
//Boots up the embedded web server (Tomcat)
//Opens the port defined in application.yml
//Equivalent to: app.listen(PORT);  in mern stack


