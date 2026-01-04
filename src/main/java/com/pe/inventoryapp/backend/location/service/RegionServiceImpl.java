package com.pe.inventoryapp.backend.location.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.location.model.entity.Region;
import com.pe.inventoryapp.backend.location.model.mapper.RegionMapper;
import com.pe.inventoryapp.backend.location.model.request.RegionRequest;
import com.pe.inventoryapp.backend.location.model.response.RegionResponse;
import com.pe.inventoryapp.backend.location.repository.RegionRepository;

@Service
public class RegionServiceImpl implements RegionService {

  @Autowired
  private RegionRepository regionRepository;

  @Override
  @Transactional
  public void saveRegion(RegionRequest regionRequest) {
    String name = regionRequest.getName().trim();

    verifyRegionNameExist(name);

    Region region = new Region();
    region.setName(name);

    regionRepository.save(region);
  }

  @Override
  @Transactional(readOnly = true)
  public List<RegionResponse> findAllRegions() {
    List<Region> regions = (List<Region>) regionRepository.findAll();

    return regions.stream()
        .map(region -> RegionMapper.builder().setRegion(region).buildRegionResponse())
        .collect(Collectors.toList());
  }

  @Override
  public RegionResponse findRegionById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_ERROR);
    }

    Region region = regionRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.ENTITY_NOT_FOUND, "La región no existe en el sistema"));

    return RegionMapper.builder().setRegion(region).buildRegionResponse();
  }

  @Override
  public void updateRegionById(Long id, RegionRequest regionRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "Esta región no se puede editar");
    }

    Region region = regionRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.ENTITY_NOT_FOUND, "La región no existe en el sistema"));

    verifyRegionNameExistById(regionRequest.getName().trim(), id);

    region.setName(regionRequest.getName().trim());

    regionRepository.save(region);
  }

  // METODOS AUXILIARES
  private void verifyRegionNameExist(String name) {
    if (regionRepository.existsByName(name)) {
      throw new FieldValidation("name", "La región con ese nombre ya existe, introduzca otro nombre");
    }
  }

  private void verifyRegionNameExistById(String name, Long id) {
    if (regionRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "La región con ese nombre ya existe, introduzca otro nombre");
    }
  }
}
