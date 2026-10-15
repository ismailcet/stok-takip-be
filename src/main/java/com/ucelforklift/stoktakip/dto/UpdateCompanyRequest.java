package com.ucelforklift.stoktakip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 500) String address,
        @NotBlank @Size(max = 100) String taxOffice,
        @NotBlank @Pattern(regexp = "^(\\d{10}|\\d{11})$", message = "Vergi numarasi 10 veya 11 haneli olmali") String taxNumber,
        @Pattern(regexp = "^$|^[0-9+()\\-\\s]{7,20}$", message = "Telefon numarasi gecersiz") String phone
) {
}