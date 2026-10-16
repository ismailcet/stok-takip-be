package com.ucelforklift.stoktakip.dto;

import com.ucelforklift.stoktakip.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank String fullName,
        @NotNull Role role
) {
}