package com.ucelforklift.stoktakip.controller;

import com.ucelforklift.stoktakip.dto.CompanyResponse;
import com.ucelforklift.stoktakip.dto.CreateCompanyRequest;
import com.ucelforklift.stoktakip.entity.Company;
import com.ucelforklift.stoktakip.repository.CompanyRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyRepository companyRepository;

    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public List<CompanyResponse> list() {
        return companyRepository.findAll().stream()
                .map(c -> new CompanyResponse(c.getId(), c.getName()))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('MASTER')")
    public CompanyResponse create(@Valid @RequestBody CreateCompanyRequest request) {
        Company company = new Company();
        company.setName(request.name());
        Company saved = companyRepository.save(company);
        return new CompanyResponse(saved.getId(), saved.getName());
    }
}