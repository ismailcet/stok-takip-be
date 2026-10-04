package com.ucelforklift.stoktakip.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotNull Long userId,
        @Pattern(regexp = "\\d{4}", message = "PIN 4 haneli olmali") String pin
) {
}