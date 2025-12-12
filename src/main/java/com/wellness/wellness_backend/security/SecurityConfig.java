package com.wellness.wellness_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Allow all common auth paths (wildcard) so small path variants won't break things
                .requestMatchers("/api/users/auth/**", "/api/auth/**", "/auth/**").permitAll()

                // Extra explicit POST permits for refresh/logout/login (redundant but clear)
                .requestMatchers(HttpMethod.POST,
                    "/api/users/auth/refresh",
                    "/api/users/auth/logout",
                    "/api/users/auth/login",
                    "/api/users/auth/register",
                    "/api/auth/refresh",
                    "/api/auth/logout",
                    "/auth/refresh",
                    "/auth/logout",
                    "/error" 
                ).permitAll()

                // Public GET reads
                .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/practitioners/**", "/api/bookings/**").permitAll()

                // PRODUCT CRUD: only PRACTITIONER or ADMIN can create/update/delete
                .requestMatchers(HttpMethod.POST, "/api/products").hasAnyRole("PRACTITIONER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("PRACTITIONER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("PRACTITIONER", "ADMIN")

                // Bookings: creating a booking requires authentication (any logged-in user)
                .requestMatchers(HttpMethod.POST, "/api/bookings/**").authenticated()

                // everything else requires authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // allow h2-console frames (dev only)
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
