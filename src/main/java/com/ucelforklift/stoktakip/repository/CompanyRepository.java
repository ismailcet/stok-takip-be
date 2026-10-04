package com.ucelforklift.stoktakip.repository;

import com.ucelforklift.stoktakip.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
