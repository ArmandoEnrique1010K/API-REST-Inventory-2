package com.pe.inventoryapp.backend.location.service;

import java.util.List;

import com.pe.inventoryapp.backend.location.model.request.SubregionRequest;
import com.pe.inventoryapp.backend.location.model.response.SubregionResponse;

public interface SubregionService {
  // TODO: PODRIA SER UNA SEGUNDA OPCION
  // void saveSubregionInRegionId(SubregionRequest subregionRequest, Long regionId);
  void saveSubregion(SubregionRequest subregionRequest);

  List<SubregionResponse> findAllSubregionsByRegionId(Long regionId);

  SubregionResponse findSubregionById(Long id);

  void updateSubregionById(Long id, SubregionRequest subregionRequest);
}
