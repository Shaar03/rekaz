package com.rekaz.assignment.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("dummyuser".equals(username)) {
            return User.builder()
                    .username(username)
                    .password("$2a$10$7WbFQVaxwQlmMjR75nbSPu/NtP/0eGxzcm0hF/P9OTYzBJPex7VQu")
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
