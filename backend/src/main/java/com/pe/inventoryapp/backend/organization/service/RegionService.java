package com.pe.inventoryapp.backend.organization.service;

import com.pe.inventoryapp.backend.organization.model.request.RegionRequest;
import com.pe.inventoryapp.backend.organization.model.response.RegionResponse;

import java.util.List;
import java.util.Optional;

public interface RegionService {
    // Guarda una region
    public String save(RegionRequest regionRequest);

    // Listar las regiones
    public List<RegionResponse> findAll();

    public Optional<RegionResponse> findById(Long id);

    // Actualizar
    public String update(Long id, RegionRequest regionRequest);

    public void verifyRegionNameExist(String name);
}
