package com.ucelforklift.stoktakip.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePartRequest(
        @NotBlank String name,
        String barcode,
        String productCode,
        String brand,
        String category,
        @NotNull Long cabinetId,
        @Min(0) Integer stockQuantity
) {
}