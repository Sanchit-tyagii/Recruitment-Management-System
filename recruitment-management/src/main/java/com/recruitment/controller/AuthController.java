package com.recruitment.controller;

import com.recruitment.model.LoginRequest;
import com.recruitment.model.SignupRequest;
import com.recruitment.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        String res = authService.signup(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String token = authService.login(req);
        return ResponseEntity.ok().body(new AuthResponse(token));
    }

    static record AuthResponse(String token) {}
}
