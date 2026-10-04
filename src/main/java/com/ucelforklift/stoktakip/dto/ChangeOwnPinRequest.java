package com.ucelforklift.stoktakip.dto;

import jakarta.validation.constraints.Pattern;

public record ChangeOwnPinRequest(
        @Pattern(regexp = "\\d{4}") String currentPin,
        @Pattern(regexp = "\\d{4}", message = "Yeni PIN 4 haneli olmali") String newPin
) {
}