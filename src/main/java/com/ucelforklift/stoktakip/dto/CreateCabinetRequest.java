package com.ucelforklift.stoktakip.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCabinetRequest(@NotBlank String name) {
}