package com.ucelforklift.stoktakip.service;

import com.ucelforklift.stoktakip.dto.MaintenanceRecordResponse;
import com.ucelforklift.stoktakip.entity.Company;
import com.ucelforklift.stoktakip.entity.MaintenanceRecord;
import com.ucelforklift.stoktakip.entity.User;
import com.ucelforklift.stoktakip.repository.CompanyRepository;
import com.ucelforklift.stoktakip.repository.MaintenanceRecordRepository;
import com.ucelforklift.stoktakip.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceRecordService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public MaintenanceRecordService(
            MaintenanceRecordRepository maintenanceRecordRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public List<MaintenanceRecordResponse> list(Long companyId) {
        var records = (companyId == null)
                ? maintenanceRecordRepository.findAllByOrderByVisitDateDesc()
                : maintenanceRecordRepository.findByCompany_IdOrderByVisitDateDesc(companyId);

        return records.stream().map(this::toResponse).toList();
    }

    public MaintenanceRecordResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public byte[] getPhoto(Long id) {
        return findOrThrow(id).getWorkOrderPhoto();
    }

    public MaintenanceRecordResponse create(Long companyId, LocalDate visitDate, Long userId, MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fotograf gerekli");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Firma bulunamadi"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kullanici bulunamadi"));

        MaintenanceRecord record = new MaintenanceRecord();
        record.setCompany(company);
        record.setVisitDate(visitDate);
        record.setRecordedByUser(user);
        try {
            record.setWorkOrderPhoto(photo.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fotograf okunamadi");
        }

        return toResponse(maintenanceRecordRepository.save(record));
    }

    private MaintenanceRecord findOrThrow(Long id) {
        return maintenanceRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bakim kaydi bulunamadi"));
    }

    private MaintenanceRecordResponse toResponse(MaintenanceRecord r) {
        Company c = r.getCompany();
        return new MaintenanceRecordResponse(
                r.getId(),
                c.getId(),
                c.getName(),
                c.getAddress(),
                c.getTaxOffice(),
                c.getTaxNumber(),
                c.getPhone(),
                r.getVisitDate(),
                r.getRecordedByUser().getId(),
                r.getRecordedByUser().getFullName(),
                r.getCreatedAt()
        );
    }
}