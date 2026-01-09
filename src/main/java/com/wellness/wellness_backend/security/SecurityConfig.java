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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow frontend origin
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001"
        ));
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Expose headers
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type"
        ));
        
        // Max age for preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (we're using JWT tokens)
            .csrf(csrf -> csrf.disable())
            
            // Enable CORS with our configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
            		
        		// ALLOW PREFLIGHT REQUESTS
        	    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            		
                // ============================================
                // WEBSOCKET ENDPOINTS - MUST BE FIRST!
                // ============================================
                .requestMatchers(
                    "/ws/**",           // WebSocket endpoint
                    "/app/**",          // STOMP app destinations
                    "/topic/**",        // STOMP topic destinations
                    "/user/**"          // STOMP user destinations
                ).permitAll()

                // ============================================
                // PUBLIC AUTH ENDPOINTS
                // ============================================
                .requestMatchers(
                    "/api/users/auth/**",
                    "/api/auth/**",
                    "/error"
                ).permitAll()

                // ============================================
                // PUBLIC READ ENDPOINTS
                // ============================================
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/products/**",
                    "/api/practitioners/**"
                ).permitAll()

                // ============================================
                // AUTHENTICATED USER ENDPOINTS
                // ============================================
                
                // User Profile
                .requestMatchers("/api/users/**").authenticated()

                // Cart
                .requestMatchers("/api/cart/**").authenticated()

                // Bookings
                .requestMatchers("/api/bookings/**").authenticated()

                // Orders
                .requestMatchers("/api/orders/**").authenticated()

                // Reviews
                .requestMatchers("/api/reviews/**").authenticated()

                // Community Q&A
                .requestMatchers("/api/qa/**").authenticated()

                // Recommendations
                .requestMatchers("/api/recommendations/**").authenticated()

                // Notifications
                .requestMatchers("/api/notifications/**").authenticated()

                // ============================================
                // PRODUCT WRITE OPERATIONS
                // ============================================
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
                .requestMatchers(
            	    HttpMethod.POST,
            	    "/api/practitioners/**"
            	).authenticated()

                // ============================================
                // ADMIN ENDPOINTS
                // ============================================
                .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")

                 // ================================
                 // USER → PRACTITIONER ENDPOINTS
                 // ================================
                 .requestMatchers(
                     HttpMethod.POST,
                     "/api/users/practitioners/**"
                 ).authenticated()

                 .requestMatchers(
                     HttpMethod.PUT,
                     "/api/users/practitioners/**"
                 ).authenticated()
                 
                 
                // ============================================
                // ALL OTHER REQUESTS
                // ============================================
                .anyRequest().authenticated()
            )
            
            // Stateless session management (JWT tokens)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Add JWT filter before username/password authentication
            .addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}