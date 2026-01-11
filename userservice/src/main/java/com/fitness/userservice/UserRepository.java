package com.fitness.userservice;

import com.fitness.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Boolean existsByEmail(String email);  
    //no need to write db query coz JPA is so good that it automatically 
    //generates query for this and then checks in database
    //Thus now service file doesn't have to directly interact with db

    Boolean existsByKeycloakId(String userId);

    User findByEmail(String email);
}
