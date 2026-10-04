package com.ucelforklift.stoktakip.dto;

public record PartResponse(
        Long id,
        String name,
        String barcode,
        String productCode,
        String brand,
        String category,
        Long cabinetId,
        String cabinetName,
        Integer stockQuantity
) {
}