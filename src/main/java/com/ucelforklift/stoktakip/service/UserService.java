package com.ucelforklift.stoktakip.service;

import com.ucelforklift.stoktakip.dto.*;
import com.ucelforklift.stoktakip.entity.User;
import com.ucelforklift.stoktakip.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserSummary> list() {
        return userRepository.findAll().stream()
                .map(u -> new UserSummary(u.getId(), u.getFullName(), u.getRole()))
                .toList();
    }

    public UserSummary create(CreateUserRequest request) {
        User user = new User();
        user.setFullName(request.fullName());
        user.setRole(request.role());
        user.setPinHash(passwordEncoder.encode(request.pin()));
        User saved = userRepository.save(user);
        return new UserSummary(saved.getId(), saved.getFullName(), saved.getRole());
    }

    public void changeOwnPin(Long userId, ChangeOwnPinRequest request) {
        User user = findUserOrThrow(userId);
        if (!passwordEncoder.matches(request.currentPin(), user.getPinHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mevcut PIN hatali");
        }
        user.setPinHash(passwordEncoder.encode(request.newPin()));
        userRepository.save(user);
    }

    public void resetPin(Long userId, ResetPinRequest request) {
        User user = findUserOrThrow(userId);
        user.setPinHash(passwordEncoder.encode(request.newPin()));
        userRepository.save(user);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanici bulunamadi"));
    }
}