package com.ucelforklift.stoktakip.controller;

import com.ucelforklift.stoktakip.dto.CompanyResponse;
import com.ucelforklift.stoktakip.dto.CreateCompanyRequest;
import com.ucelforklift.stoktakip.dto.UpdateCompanyRequest;
import com.ucelforklift.stoktakip.dto.UpdateCompanyVisibilityRequest;
import com.ucelforklift.stoktakip.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Varsayilan olarak gizli firmalar da doner (yonetim ekrani ve bakim filtresi icin).
     * Yeni bakim kaydi ekranlari activeOnly=true ile gizli firmalari disarida birakir.
     */
    @GetMapping
    public List<CompanyResponse> list(@RequestParam(defaultValue = "false") boolean activeOnly) {
        return companyService.list(activeOnly);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse create(@Valid @RequestBody CreateCompanyRequest request) {
        return companyService.create(request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCompanyRequest request) {
        return companyService.update(id, request);
    }

    @PatchMapping("/{id}/visibility")
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse updateVisibility(@PathVariable Long id,
                                            @Valid @RequestBody UpdateCompanyVisibilityRequest request) {
        return companyService.updateVisibility(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}