package com.pe.inventoryapp.backend.organization.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.organization.model.entity.Region;
import com.pe.inventoryapp.backend.organization.model.mapper.RegionMapper;
import com.pe.inventoryapp.backend.organization.model.request.RegionRequest;
import com.pe.inventoryapp.backend.organization.model.response.RegionResponse;
import com.pe.inventoryapp.backend.organization.repository.RegionRepository;

@Service
public class RegionServiceImpl implements RegionService {

  @Autowired
  private RegionRepository regionRepository;

  @Override
  @Transactional
  public String save(RegionRequest regionRequest) {
    Region region = new Region();
    region.setName(regionRequest.getName());

    regionRepository.save(region);
    return "Se guardo la región";

  }

  @Override
  @Transactional(readOnly = true)
  public List<RegionResponse> findAll() {
    List<Region> regions = (List<Region>) regionRepository.findAll();

    return regions.stream()
        .map(region -> RegionMapper.builder().setRegion(region).buildRegionResponse())
        .collect(Collectors.toList());
  }

  @Override
  public Optional<RegionResponse> findById(Long id) {
    return regionRepository.findById(id)
        .map(region -> RegionMapper.builder().setRegion(region).buildRegionResponse());
  }

  @Override
  public String update(Long id, RegionRequest regionRequest) {
    Optional<Region> regionById = regionRepository.findById(id);

    // Category categoryOptional = null;

    // Si la categoria existe, lo actualiza con los datos proporcionados
    if (regionById.isPresent()) {
      Region regionData = regionById.orElseThrow();

      regionData.setName(regionRequest.getName());

      // categoryOptional = categoryRepository.save(categoryData);
      regionRepository.save(regionData);
    }

    // Optional.ofNullable(CategoryMapper.builder().setCategory(categoryOptional).buildListCategoriesResponse());
    return "Se actualizo la región";

  }

  @Override
  public void verifyRegionNameExist(String name) {
    if (regionRepository.findByName(name).isPresent()) {
      throw new FieldValidation("name", "La region con ese nombre ya existe, inserte otra region");
    }
  }

}
