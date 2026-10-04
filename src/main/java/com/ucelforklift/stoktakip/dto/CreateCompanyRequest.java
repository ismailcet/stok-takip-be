package com.ucelforklift.stoktakip.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCompanyRequest(@NotBlank String name) {
}