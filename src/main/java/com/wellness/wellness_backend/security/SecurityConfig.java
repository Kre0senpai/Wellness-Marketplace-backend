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
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                // PUBLIC AUTH
                .requestMatchers(
                    "/api/users/auth/**",
                    "/api/auth/**",
                    "/error"
                ).permitAll()

                // PUBLIC READ
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/products/**",
                    "/api/practitioners/**"
                ).permitAll()

                // CART
                .requestMatchers("/api/cart/**").authenticated()

                // BOOKINGS
                .requestMatchers("/api/bookings/**").authenticated()

                // ORDERS (🔥 THIS WAS MISSING)
                .requestMatchers("/api/orders/**").authenticated()

                // PRODUCT WRITE
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/products"
                ).authenticated()
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/products/**"
                ).authenticated()
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/products/**"
                ).authenticated()

                // ADMIN
                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
