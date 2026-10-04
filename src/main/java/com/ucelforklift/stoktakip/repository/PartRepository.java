package com.ucelforklift.stoktakip.repository;

import com.ucelforklift.stoktakip.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {

    Optional<Part> findByBarcode(String barcode);

    Optional<Part> findByProductCode(String productCode);

    List<Part> findByNameContainingIgnoreCaseOrBarcodeContainingIgnoreCase(String name, String barcode);
}
