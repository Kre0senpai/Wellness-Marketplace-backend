package com.wellness.wellness_backend.service;

//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import com.wellness.wellness_backend.repo.UserRepository;
import com.wellness.wellness_backend.model.User;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User u) {
        // basic email check
        if (repo.findByEmail(u.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return repo.save(u);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }
    
    public User getByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }
    
    public User getById(Long id) {
        return repo.findById(id).orElse(null);
    }


}
