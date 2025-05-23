package com.dailycodework.dreamshops.repository;

import com.dailycodework.dreamshops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLastName(String lastName);

    User findByFirstName(String firstName);

    User findByEmail(String email);

    boolean existsByEmail(String email);

}