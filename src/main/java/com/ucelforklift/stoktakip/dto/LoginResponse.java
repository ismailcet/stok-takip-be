package com.ucelforklift.stoktakip.dto;

import com.ucelforklift.stoktakip.entity.Role;

public record LoginResponse(String token, Long id, String fullName, Role role) {
}