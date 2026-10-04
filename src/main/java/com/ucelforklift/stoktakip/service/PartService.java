package com.ucelforklift.stoktakip.service;

import com.ucelforklift.stoktakip.dto.CreatePartRequest;
import com.ucelforklift.stoktakip.dto.PartResponse;
import com.ucelforklift.stoktakip.dto.UpdatePartRequest;
import com.ucelforklift.stoktakip.entity.Cabinet;
import com.ucelforklift.stoktakip.entity.Part;
import com.ucelforklift.stoktakip.repository.CabinetRepository;
import com.ucelforklift.stoktakip.repository.PartRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PartService {

    private final PartRepository partRepository;
    private final CabinetRepository cabinetRepository;
    private final int lowStockThreshold;

    public PartService(
            PartRepository partRepository,
            CabinetRepository cabinetRepository,
            @Value("${app.low-stock-threshold:5}") int lowStockThreshold) {
        this.partRepository = partRepository;
        this.cabinetRepository = cabinetRepository;
        this.lowStockThreshold = lowStockThreshold;
    }

    public List<PartResponse> list(String search, boolean lowStockOnly) {
        List<Part> parts = (search == null || search.isBlank())
                ? partRepository.findAll()
                : partRepository.findByNameContainingIgnoreCaseOrBarcodeContainingIgnoreCase(search, search);

        return parts.stream()
                .filter(p -> !lowStockOnly || p.getStockQuantity() <= lowStockThreshold)
                .map(this::toResponse)
                .toList();
    }

    public PartResponse getById(Long id) {
        return toResponse(findPartOrThrow(id));
    }

    public PartResponse lookup(String barcode, String productCode) {
        Part part;
        if (barcode != null && !barcode.isBlank()) {
            part = partRepository.findByBarcode(barcode)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barkod bulunamadi"));
        } else if (productCode != null && !productCode.isBlank()) {
            part = partRepository.findByProductCode(productCode)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Urun kodu bulunamadi"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "barcode veya productCode gerekli");
        }
        return toResponse(part);
    }

    public PartResponse create(CreatePartRequest request) {
        if (isBlank(request.barcode()) && isBlank(request.productCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barkod veya urun kodundan biri girilmeli");
        }

        Cabinet cabinet = cabinetRepository.findById(request.cabinetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dolap bulunamadi"));

        Part part = new Part();
        part.setName(request.name());
        part.setBarcode(blankToNull(request.barcode()));
        part.setProductCode(blankToNull(request.productCode()));
        part.setBrand(request.brand());
        part.setCategory(request.category());
        part.setCabinet(cabinet);
        part.setStockQuantity(request.stockQuantity() == null ? 0 : request.stockQuantity());

        return toResponse(partRepository.save(part));
    }

    public PartResponse update(Long id, UpdatePartRequest request) {
        Part part = findPartOrThrow(id);

        Cabinet cabinet = cabinetRepository.findById(request.cabinetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dolap bulunamadi"));

        part.setCabinet(cabinet);
        part.setStockQuantity(request.stockQuantity());
        part.setBrand(request.brand());

        return toResponse(partRepository.save(part));
    }

    private Part findPartOrThrow(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parca bulunamadi"));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value;
    }

    private PartResponse toResponse(Part part) {
        return new PartResponse(
                part.getId(),
                part.getName(),
                part.getBarcode(),
                part.getProductCode(),
                part.getBrand(),
                part.getCategory(),
                part.getCabinet().getId(),
                part.getCabinet().getName(),
                part.getStockQuantity()
        );
    }
}