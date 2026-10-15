package com.ucelforklift.stoktakip.controller;

import com.ucelforklift.stoktakip.dto.CompanyResponse;
import com.ucelforklift.stoktakip.dto.CreateCompanyRequest;
import com.ucelforklift.stoktakip.dto.UpdateCompanyRequest;
import com.ucelforklift.stoktakip.dto.UpdateCompanyVisibilityRequest;
import com.ucelforklift.stoktakip.entity.Company;
import com.ucelforklift.stoktakip.repository.CompanyRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyRepository companyRepository;

    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * Varsayilan olarak gizli firmalar da doner (yonetim ekrani ve bakim filtresi icin).
     * Yeni bakim kaydi ekranlari activeOnly=true ile gizli firmalari disarida birakir.
     */
    @GetMapping
    public List<CompanyResponse> list(@RequestParam(defaultValue = "false") boolean activeOnly) {
        return companyRepository.findAll().stream()
                .filter(c -> !activeOnly || !c.isHidden())
                .sorted(Comparator.comparing(Company::getName, String.CASE_INSENSITIVE_ORDER))
                .map(CompanyResponse::from)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse create(@Valid @RequestBody CreateCompanyRequest request) {
        String name = request.name().trim();
        if (companyRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu isimde bir firma zaten var");
        }
        Company company = new Company();
        apply(company, name, request.address(), request.taxOffice(), request.taxNumber(), request.phone());
        return CompanyResponse.from(companyRepository.save(company));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Firma bulunamadi"));
        String name = request.name().trim();
        if (companyRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu isimde bir firma zaten var");
        }
        apply(company, name, request.address(), request.taxOffice(), request.taxNumber(), request.phone());
        return CompanyResponse.from(companyRepository.save(company));
    }

    @PatchMapping("/{id}/visibility")
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse updateVisibility(@PathVariable Long id,
                                            @Valid @RequestBody UpdateCompanyVisibilityRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Firma bulunamadi"));
        company.setHidden(request.hidden());
        return CompanyResponse.from(companyRepository.save(company));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Firma bulunamadi"));
        // Bakim kaydi olan firmada FK ihlali olusur, GlobalExceptionHandler bunu 409 yapar.
        companyRepository.delete(company);
        return ResponseEntity.noContent().build();
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