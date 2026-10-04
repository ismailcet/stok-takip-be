package com.ucelforklift.stoktakip.service;

import com.ucelforklift.stoktakip.dto.CabinetResponse;
import com.ucelforklift.stoktakip.dto.CreateCabinetRequest;
import com.ucelforklift.stoktakip.entity.Cabinet;
import com.ucelforklift.stoktakip.repository.CabinetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CabinetService {

    private final CabinetRepository cabinetRepository;

    public CabinetService(CabinetRepository cabinetRepository) {
        this.cabinetRepository = cabinetRepository;
    }

    public List<CabinetResponse> list() {
        return cabinetRepository.findAll().stream()
                .map(c -> new CabinetResponse(c.getId(), c.getName()))
                .toList();
    }

    public CabinetResponse create(CreateCabinetRequest request) {
        if (cabinetRepository.existsByNameIgnoreCase(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu isimde bir dolap zaten var");
        }
        Cabinet cabinet = new Cabinet();
        cabinet.setName(request.name());
        Cabinet saved = cabinetRepository.save(cabinet);
        return new CabinetResponse(saved.getId(), saved.getName());
    }

    public CabinetResponse rename(Long id, CreateCabinetRequest request) {
        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dolap bulunamadi"));
        cabinet.setName(request.name());
        return new CabinetResponse(cabinet.getId(), cabinetRepository.save(cabinet).getName());
    }

    public void delete(Long id) {
        if (!cabinetRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dolap bulunamadi");
        }
        cabinetRepository.deleteById(id);
    }
}