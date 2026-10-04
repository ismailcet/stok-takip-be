package com.ucelforklift.stoktakip.dto;

import com.ucelforklift.stoktakip.entity.Role;

public record UserSummary(Long id, String fullName, Role role) {
}