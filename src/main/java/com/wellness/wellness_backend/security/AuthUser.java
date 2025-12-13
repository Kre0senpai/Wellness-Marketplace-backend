package com.wellness.wellness_backend.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public class AuthUser {

    private final Long userId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(Long userId, String username,
                    Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
