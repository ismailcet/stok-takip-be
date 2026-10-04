package com.ucelforklift.stoktakip.controller;

import com.ucelforklift.stoktakip.dto.MaintenanceRecordResponse;
import com.ucelforklift.stoktakip.service.MaintenanceRecordService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance-records")
public class MaintenanceRecordController {

    private final MaintenanceRecordService maintenanceRecordService;

    public MaintenanceRecordController(MaintenanceRecordService maintenanceRecordService) {
        this.maintenanceRecordService = maintenanceRecordService;
    }

    @GetMapping
    public List<MaintenanceRecordResponse> list(@RequestParam(required = false) Long companyId) {
        return maintenanceRecordService.list(companyId);
    }

    @GetMapping("/{id}")
    public MaintenanceRecordResponse getById(@PathVariable Long id) {
        return maintenanceRecordService.getById(id);
    }

    @GetMapping(value = "/{id}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        byte[] photo = maintenanceRecordService.getPhoto(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photo);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MaintenanceRecordResponse create(
            Authentication authentication,
            @RequestParam Long companyId,
            @RequestParam LocalDate visitDate,
            @RequestPart MultipartFile photo) {
        Long userId = (Long) authentication.getPrincipal();
        return maintenanceRecordService.create(companyId, visitDate, userId, photo);
    }
}