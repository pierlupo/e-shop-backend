package com.eShop.repository;

import com.eShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLastname(String lastname);

    User findByFirstname(String firstname);

    User findByEmail(String email);

    boolean existsByEmail(String email);

}