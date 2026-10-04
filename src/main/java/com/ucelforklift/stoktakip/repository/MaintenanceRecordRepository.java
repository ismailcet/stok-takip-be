package com.ucelforklift.stoktakip.repository;

import com.ucelforklift.stoktakip.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    List<MaintenanceRecord> findAllByOrderByVisitDateDesc();

    List<MaintenanceRecord> findByCompany_IdOrderByVisitDateDesc(Long companyId);
}
