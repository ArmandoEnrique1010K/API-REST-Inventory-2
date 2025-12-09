package com.pe.inventoryapp.backend.logistics.service;

import com.pe.inventoryapp.backend.logistics.model.request.RegionRequest;
import com.pe.inventoryapp.backend.logistics.model.response.RegionResponse;
import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;

import java.util.List;
import java.util.Optional;

public interface RegionService {
    // Guarda una region
    public String save(RegionRequest regionRequest);

    // Listar las regiones
    public List<RegionResponse> findAll();

    public List<RegionResponse> findAllByStatusTrue();

    public Optional<CategoryResponse> findById(Long id);

    // Actualizar
    public void changeStatus(Long id);

    public String update(Long id, CategoryRequest categoryRequest);

    public void verifyCategoryNameExist(String name);

}
