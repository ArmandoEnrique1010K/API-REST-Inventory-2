package com.pe.inventoryapp.backend.location.service;

import java.util.List;

import com.pe.inventoryapp.backend.location.model.request.SubregionRequest;
import com.pe.inventoryapp.backend.location.model.response.ListSubregionResponse;
import com.pe.inventoryapp.backend.location.model.response.SubregionResponse;

public interface SubregionService {
  void saveSubregion(SubregionRequest subregionRequest);

  List<SubregionResponse> findAllSubregionsByRegionId(Long regionId);

  SubregionResponse findSubregionById(Long id);

  void updateSubregionById(Long id, SubregionRequest subregionRequest);

    List<ListSubregionResponse> findAllSubregionsByDeliveryOrder(Long deliveryOrderId, Long regionId);
}
