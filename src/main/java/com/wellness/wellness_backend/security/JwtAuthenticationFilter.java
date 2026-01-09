package com.wellness.wellness_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // NO TOKEN → JUST CONTINUE
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);

            if (!jwtUtil.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.getUsername(token);

            if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            	var authorities = List.of(
            		    new SimpleGrantedAuthority(
            		        "ROLE_" + jwtUtil.getRole(token).toUpperCase()
            		    )
            		);

        		AuthUser authUser = new AuthUser(
        		        jwtUtil.getUserId(token),   // MUST come from JWT
        		        jwtUtil.getUsername(token),
        		        authorities
        		);

        		UsernamePasswordAuthenticationToken authentication =
        		        new UsernamePasswordAuthenticationToken(
        		                authUser,
        		                null,
        		                authorities
        		        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
