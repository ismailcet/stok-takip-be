package com.ucelforklift.stoktakip.dto;

import com.ucelforklift.stoktakip.entity.Company;

public record CompanyResponse(
        Long id,
        String name,
        String address,
        String taxOffice,
        String taxNumber,
        String phone,
        boolean hidden
) {
    public static CompanyResponse from(Company c) {
        return new CompanyResponse(
                c.getId(),
                c.getName(),
                c.getAddress(),
                c.getTaxOffice(),
                c.getTaxNumber(),
                c.getPhone(),
                c.isHidden()
        );
    }
}