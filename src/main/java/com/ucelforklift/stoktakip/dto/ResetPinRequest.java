package com.ucelforklift.stoktakip.dto;

import jakarta.validation.constraints.Pattern;

public record ResetPinRequest(
        @Pattern(regexp = "\\d{4}", message = "PIN 4 haneli olmali") String newPin
) {
}