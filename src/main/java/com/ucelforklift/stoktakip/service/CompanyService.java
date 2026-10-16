package com.ucelforklift.stoktakip.service;

import com.ucelforklift.stoktakip.config.CacheNames;
import com.ucelforklift.stoktakip.dto.CompanyResponse;
import com.ucelforklift.stoktakip.dto.CreateCompanyRequest;
import com.ucelforklift.stoktakip.dto.UpdateCompanyRequest;
import com.ucelforklift.stoktakip.dto.UpdateCompanyVisibilityRequest;
import com.ucelforklift.stoktakip.entity.Company;
import com.ucelforklift.stoktakip.repository.CompanyRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * Varsayilan olarak gizli firmalar da doner (yonetim ekrani ve bakim filtresi icin).
     * Yeni bakim kaydi ekranlari activeOnly=true ile gizli firmalari disarida birakir.
     */
    @Cacheable(cacheNames = CacheNames.COMPANIES, key = "#activeOnly")
    public List<CompanyResponse> list(boolean activeOnly) {
        return companyRepository.findAll().stream()
                .filter(c -> !activeOnly || !c.isHidden())
                .sorted(Comparator.comparing(Company::getName, String.CASE_INSENSITIVE_ORDER))
                .map(CompanyResponse::from)
                .toList();
    }

    @CacheEvict(cacheNames = CacheNames.COMPANIES, allEntries = true)
    public CompanyResponse create(CreateCompanyRequest request) {
        String name = request.name().trim();
        if (companyRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu isimde bir firma zaten var");
        }
        Company company = new Company();
        apply(company, name, request.address(), request.taxOffice(), request.taxNumber(), request.phone());
        return CompanyResponse.from(companyRepository.save(company));
    }

    @CacheEvict(cacheNames = CacheNames.COMPANIES, allEntries = true)
    public CompanyResponse update(Long id, UpdateCompanyRequest request) {
        Company company = findOrThrow(id);
        String name = request.name().trim();
        if (companyRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu isimde bir firma zaten var");
        }
        apply(company, name, request.address(), request.taxOffice(), request.taxNumber(), request.phone());
        return CompanyResponse.from(companyRepository.save(company));
    }

    @CacheEvict(cacheNames = CacheNames.COMPANIES, allEntries = true)
    public CompanyResponse updateVisibility(Long id, UpdateCompanyVisibilityRequest request) {
        Company company = findOrThrow(id);
        company.setHidden(request.hidden());
        return CompanyResponse.from(companyRepository.save(company));
    }

    @CacheEvict(cacheNames = CacheNames.COMPANIES, allEntries = true)
    public void delete(Long id) {
        Company company = findOrThrow(id);
        // Bakim kaydi olan firmada FK ihlali olusur, GlobalExceptionHandler bunu 409 yapar.
        companyRepository.delete(company);
    }

    private Company findOrThrow(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Firma bulunamadi"));
    }

    private static void apply(Company company, String name, String address, String taxOffice,
                              String taxNumber, String phone) {
        company.setName(name);
        company.setAddress(address.trim());
        company.setTaxOffice(taxOffice.trim());
        company.setTaxNumber(taxNumber.trim());
        company.setPhone(phone == null || phone.isBlank() ? null : phone.trim());
    }
}