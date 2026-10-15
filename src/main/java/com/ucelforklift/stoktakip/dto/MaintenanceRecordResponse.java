package com.ucelforklift.stoktakip.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MaintenanceRecordResponse(
        Long id,
        Long companyId,
        String companyName,
        String companyAddress,
        String companyTaxOffice,
        String companyTaxNumber,
        String companyPhone,
        LocalDate visitDate,
        Long recordedByUserId,
        String recordedByUserName,
        LocalDateTime createdAt
) {
}