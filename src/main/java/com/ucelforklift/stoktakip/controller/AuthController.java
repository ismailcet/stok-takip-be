package com.ucelforklift.stoktakip.controller;

import com.ucelforklift.stoktakip.dto.LoginRequest;
import com.ucelforklift.stoktakip.dto.LoginResponse;
import com.ucelforklift.stoktakip.entity.User;
import com.ucelforklift.stoktakip.repository.UserRepository;
import com.ucelforklift.stoktakip.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findById(request.userId())
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kullanici bulunamadi"));

        if (!passwordEncoder.matches(request.pin(), user.getPinHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "PIN hatali");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole().name());
        return new LoginResponse(token, user.getId(), user.getFullName(), user.getRole());
    }
}