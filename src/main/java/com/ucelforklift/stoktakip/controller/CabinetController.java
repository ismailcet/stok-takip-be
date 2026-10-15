package com.ucelforklift.stoktakip.controller;

import com.ucelforklift.stoktakip.dto.CabinetResponse;
import com.ucelforklift.stoktakip.dto.CreateCabinetRequest;
import com.ucelforklift.stoktakip.service.CabinetService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cabinets")
public class CabinetController {

    private final CabinetService cabinetService;

    public CabinetController(CabinetService cabinetService) {
        this.cabinetService = cabinetService;
    }

    @GetMapping
    public List<CabinetResponse> list() {
        return cabinetService.list();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CabinetResponse create(@Valid @RequestBody CreateCabinetRequest request) {
        return cabinetService.create(request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CabinetResponse rename(@PathVariable Long id, @Valid @RequestBody CreateCabinetRequest request) {
        return cabinetService.rename(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        cabinetService.delete(id);
    }
}