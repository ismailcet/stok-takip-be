package com.ucelforklift.stoktakip.repository;

import com.ucelforklift.stoktakip.entity.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CabinetRepository extends JpaRepository<Cabinet, Long> {

    boolean existsByNameIgnoreCase(String name);
}
