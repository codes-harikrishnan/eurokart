package com.harikrishnan.eurokart.user.repository;

import com.harikrishnan.eurokart.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User,Long> {
    User findUserByEmail(String email);

    boolean existsUserByEmail(String email);

    User findUserById(Long id);
}
