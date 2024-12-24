package com.rekaz.assignment.components.auth.controller;

import com.rekaz.assignment.components.auth.request.LoginRequest;
import com.rekaz.assignment.config.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        //dummy logic for simplicity
        if ("dummyuser".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            String token = jwtUtil.generateAccessToken(request.getUsername());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
