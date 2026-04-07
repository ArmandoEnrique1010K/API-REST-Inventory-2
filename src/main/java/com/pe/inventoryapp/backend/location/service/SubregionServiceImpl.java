package com.pe.inventoryapp.backend.location.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.location.model.entity.Region;
import com.pe.inventoryapp.backend.location.model.entity.Subregion;
import com.pe.inventoryapp.backend.location.model.mapper.SubregionMapper;
import com.pe.inventoryapp.backend.location.model.request.SubregionRequest;
import com.pe.inventoryapp.backend.location.model.response.SubregionResponse;
import com.pe.inventoryapp.backend.location.repository.RegionRepository;
import com.pe.inventoryapp.backend.location.repository.SubregionRepository;

@Service
public class SubregionServiceImpl implements SubregionService{

  private final SubregionRepository subregionRepository;
  private final RegionRepository regionRepository;
  private final SubregionDomainService subregionDomainService;

  public SubregionServiceImpl(
    SubregionRepository subregionRepository,
    RegionRepository regionRepository,
    SubregionDomainService subregionDomainService
  ) {
    this.subregionRepository = subregionRepository;
    this.regionRepository = regionRepository;
    this.subregionDomainService = subregionDomainService;
  }

  @Override
  @Transactional
  public void saveSubregion(SubregionRequest subregionRequest) {
    String name = subregionRequest.getName().trim();
    Long regionId = subregionRequest.getRegionId();
    
    subregionDomainService.verifySubregionNameAvailableByRegionId(name, regionId);

        if (regionId == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }
    Region region = regionRepository.findById(
        regionId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND,
            "El producto no existe"));

    Subregion subregion = new Subregion();
    subregion.setName(name);
    subregion.setRegion(region);
    subregionRepository.save(subregion);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SubregionResponse> findAllSubregionsByRegionId(Long regionId) {
        if (regionId == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    List<Subregion> subregions = (List<Subregion>) subregionRepository.findAllByRegionId(regionId);

    return subregions.stream()
        .map(model -> SubregionMapper.builder().setSubregion(model).buildSubregionResponse())
        .collect(Collectors.toList());

  }

  @Override
  @Transactional(readOnly = true)
  public SubregionResponse findSubregionById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Subregion subregion = subregionRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La subregión no existe"));

    return SubregionMapper.builder().setSubregion(subregion).buildSubregionResponse();
  }

  @Override
  @Transactional
  public void updateSubregionById(Long id, SubregionRequest subregionRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.CONFLICT, "Esta subregión no se puede editar");
    }

    Subregion subregion = subregionRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La subregión no existe"));

    String name = subregionRequest.getName().trim();
    Long idRegion = subregionRequest.getRegionId();

    if (idRegion == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    subregionDomainService.verifySubregionNameAvailableByRegionIdExcludingId(name, idRegion, id);

    Region region = regionRepository.findById(
        idRegion)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND,
            "La región no existe"));

    subregion.setName(name);
    subregion.setRegion(region);
    subregionRepository.save(subregion);
  }
}
