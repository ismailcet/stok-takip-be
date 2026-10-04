package com.ucelforklift.stoktakip.dto;

import com.ucelforklift.stoktakip.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(
        @NotBlank String fullName,
        @NotNull Role role,
        @Pattern(regexp = "\\d{4}", message = "PIN 4 haneli olmali") String pin
) {
}