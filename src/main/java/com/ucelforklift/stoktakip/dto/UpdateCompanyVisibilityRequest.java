package com.ucelforklift.stoktakip.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateCompanyVisibilityRequest(@NotNull Boolean hidden) {
}