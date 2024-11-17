package com.example.Demo.repository;

import com.example.Demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsernameAndPasswordAndRole(String username, String password, User.Role role);
}
