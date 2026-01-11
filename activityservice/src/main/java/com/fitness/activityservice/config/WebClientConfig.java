package com.fitness.activityservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

//Creating WebClient configuration file so Spring can create and manage
//a WebClient bean that can be injected later to call UserService.
//ActivityService can do API call to UserService using it to validate ki 
//jis UserId ne activity banai hai that does exist in postgreSQL database ie 
//user exist karta hai kisi non existent user ki id se activity banane ki 
//req nhi ai
@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("http://USER-SERVICE")
                .build();
    }
}
 