package com.ucelforklift.stoktakip.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdatePartRequest(
        @NotNull @Min(0) Integer stockQuantity,
        @NotNull Long cabinetId,
        String brand
) {
}