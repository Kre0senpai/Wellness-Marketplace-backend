package com.wellness.wellness_backend.security;

import com.wellness.wellness_backend.model.User;
import com.wellness.wellness_backend.repo.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;
    public CustomUserDetailsService(UserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = repo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // build simple UserDetails with role
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + (u.getRole() == null ? "USER" : u.getRole().toUpperCase())))
        );
    }
}
