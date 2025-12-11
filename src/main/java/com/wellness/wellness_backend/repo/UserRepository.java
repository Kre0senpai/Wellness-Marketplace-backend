package com.wellness.wellness_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wellness.wellness_backend.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
