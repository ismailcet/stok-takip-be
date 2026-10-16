package com.ucelforklift.stoktakip.controller;

import com.ucelforklift.stoktakip.dto.*;
import com.ucelforklift.stoktakip.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserSummary> list() {
        return userService.list();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserSummary create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @PatchMapping("/me/pin")
    public void changeOwnPin(Authentication authentication, @Valid @RequestBody ChangeOwnPinRequest request) {
        userService.changeOwnPin((Long) authentication.getPrincipal(), request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserSummary update(Authentication authentication,
                              @PathVariable Long id,
                              @Valid @RequestBody UpdateUserRequest request) {
        return userService.update((Long) authentication.getPrincipal(), id, request);
    }

    @PatchMapping("/{id}/pin")
    @PreAuthorize("hasRole('ADMIN')")
    public void resetPin(@PathVariable Long id, @Valid @RequestBody ResetPinRequest request) {
        userService.resetPin(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        userService.delete((Long) authentication.getPrincipal(), id);
        return ResponseEntity.noContent().build();
    }
}