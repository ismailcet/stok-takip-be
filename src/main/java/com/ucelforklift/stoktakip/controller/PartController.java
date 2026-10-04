package com.ucelforklift.stoktakip.controller;

import com.ucelforklift.stoktakip.dto.CreatePartRequest;
import com.ucelforklift.stoktakip.dto.PartResponse;
import com.ucelforklift.stoktakip.dto.UpdatePartRequest;
import com.ucelforklift.stoktakip.service.PartService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public List<PartResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean lowStockOnly) {
        return partService.list(search, lowStockOnly);
    }

    @GetMapping("/{id}")
    public PartResponse getById(@PathVariable Long id) {
        return partService.getById(id);
    }

    @GetMapping("/lookup")
    public PartResponse lookup(
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) String productCode) {
        return partService.lookup(barcode, productCode);
    }

    @PostMapping
    @PreAuthorize("hasRole('MASTER')")
    public PartResponse create(@Valid @RequestBody CreatePartRequest request) {
        return partService.create(request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('MASTER')")
    public PartResponse update(@PathVariable Long id, @Valid @RequestBody UpdatePartRequest request) {
        return partService.update(id, request);
    }
}