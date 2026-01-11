package com.fitness.aiservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
//created so that db models me @CreatedDate and @LastModifiedDate are automatically populated with values