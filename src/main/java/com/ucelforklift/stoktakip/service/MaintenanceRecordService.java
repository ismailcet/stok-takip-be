package com.ucelforklift.stoktakip.service;

import com.ucelforklift.stoktakip.dto.MaintenanceRecordResponse;
import com.ucelforklift.stoktakip.entity.Company;
import com.ucelforklift.stoktakip.entity.MaintenanceRecord;
import com.ucelforklift.stoktakip.entity.User;
import com.ucelforklift.stoktakip.repository.CompanyRepository;
import com.ucelforklift.stoktakip.repository.MaintenanceRecordRepository;
import com.ucelforklift.stoktakip.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class MaintenanceRecordService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MaintenanceRecordService(
            MaintenanceRecordRepository maintenanceRecordRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    /**
     * Filtreli liste. Fotograf sutunu okunmaz (yalnizca gereken alanlar tek sorguda gelir).
     *
     * @param companyId firma (opsiyonel)
     * @param q         firma adi / vergi dairesi / vergi no / telefon / adres / kaydeden icinde arar (opsiyonel)
     * @param dateFrom  ziyaret tarihi >= (opsiyonel)
     * @param dateTo    ziyaret tarihi <= (opsiyonel)
     */
    public List<MaintenanceRecordResponse> list(Long companyId, String q, LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Baslangic tarihi bitisten sonra olamaz");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<MaintenanceRecord> r = cq.from(MaintenanceRecord.class);
        Join<MaintenanceRecord, Company> c = r.join("company");
        Join<MaintenanceRecord, User> u = r.join("recordedByUser");

        cq.multiselect(
                r.get("id"),                // 0
                c.get("id"),                // 1
                c.get("name"),              // 2
                c.get("address"),           // 3
                c.get("taxOffice"),         // 4
                c.get("taxNumber"),         // 5
                c.get("phone"),             // 6
                r.get("visitDate"),         // 7
                u.get("id"),                // 8
                u.get("fullName"),          // 9
                r.get("createdAt")          // 10
        );

        List<Predicate> predicates = new ArrayList<>();
        if (companyId != null) {
            predicates.add(cb.equal(c.get("id"), companyId));
        }
        if (dateFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(r.get("visitDate"), dateFrom));
        }
        if (dateTo != null) {
            predicates.add(cb.lessThanOrEqualTo(r.get("visitDate"), dateTo));
        }
        if (q != null && !q.isBlank()) {
            String pattern = "%" + escapeLike(q.trim().toLowerCase(Locale.ROOT)) + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(c.get("name")), pattern, '\\'),
                    cb.like(cb.lower(c.get("address")), pattern, '\\'),
                    cb.like(cb.lower(c.get("taxOffice")), pattern, '\\'),
                    cb.like(cb.lower(c.get("taxNumber")), pattern, '\\'),
                    cb.like(cb.lower(c.get("phone")), pattern, '\\'),
                    cb.like(cb.lower(u.get("fullName")), pattern, '\\')
            ));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(r.get("visitDate")), cb.desc(r.get("id")));

        return entityManager.createQuery(cq).getResultList().stream()
                .map(t -> new MaintenanceRecordResponse(
                        t.get(0, Long.class),
                        t.get(1, Long.class),
                        t.get(2, String.class),
                        t.get(3, String.class),
                        t.get(4, String.class),
                        t.get(5, String.class),
                        t.get(6, String.class),
                        t.get(7, LocalDate.class),
                        t.get(8, Long.class),
                        t.get(9, String.class),
                        t.get(10, LocalDateTime.class)))
                .toList();
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

    /** LIKE ozel karakterlerini (\, %, _) kacislar. */
    private String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
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