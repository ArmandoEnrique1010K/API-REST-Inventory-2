package com.pe.inventoryapp.backend.location.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.location.model.entity.Region;
import com.pe.inventoryapp.backend.location.model.mapper.RegionMapper;
import com.pe.inventoryapp.backend.location.model.request.RegionRequest;
import com.pe.inventoryapp.backend.location.model.response.RegionResponse;
import com.pe.inventoryapp.backend.location.repository.RegionRepository;

@Service
public class RegionServiceImpl implements RegionService {

  private final RegionRepository regionRepository;
  private final RegionDomainService regionDomainService;

  public RegionServiceImpl(RegionRepository regionRepository, RegionDomainService regionDomainService) {
    this.regionRepository = regionRepository;
    this.regionDomainService = regionDomainService;
  }

  @Override
  @Transactional
  public void saveRegion(RegionRequest regionRequest) {
    String name = regionRequest.getName().trim();

    regionDomainService.verifyRegionNameAvailable(name);

    Region region = new Region();
    region.setName(name);

    regionRepository.save(region);
  }

  @Override
  @Transactional(readOnly = true)
  public List<RegionResponse> findAllRegions() {
    List<Region> regions = (List<Region>) regionRepository.findAllAndSortById();

    return regions.stream()
        .map(region -> RegionMapper.builder().setRegion(region).buildRegionResponse())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public RegionResponse findRegionById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Region region = regionRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La región no existe"));

    return RegionMapper.builder().setRegion(region).buildRegionResponse();
  }

  @Override
  @Transactional
  public void updateRegionById(Long id, RegionRequest regionRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.CONFLICT, "Esta región no se puede editar");
    }

    Region region = regionRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La región no existe"));

    String newName = regionRequest.getName().trim();
    regionDomainService.verifyRegionNameAvailableExcludingId(newName, id);

    region.setName(newName);
    regionRepository.save(region);
  }
}
