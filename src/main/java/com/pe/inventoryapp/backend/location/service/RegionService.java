package com.pe.inventoryapp.backend.location.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.location.model.request.RegionRequest;
import com.pe.inventoryapp.backend.location.model.response.RegionResponse;

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
