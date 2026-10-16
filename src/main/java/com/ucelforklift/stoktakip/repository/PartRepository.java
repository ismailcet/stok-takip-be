package com.ucelforklift.stoktakip.repository;

import com.ucelforklift.stoktakip.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {

    Optional<Part> findByBarcode(String barcode);

    Optional<Part> findByProductCode(String productCode);

    @Query("""
            select p from Part p
            where lower(p.name) like lower(concat('%', :q, '%')) escape '\\'
               or lower(p.productCode) like lower(concat(:q, '%')) escape '\\'
               or lower(p.barcode) like lower(concat(:q, '%')) escape '\\'
            """)
    List<Part> search(@Param("q") String q);
}