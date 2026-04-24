package com.pe.inventoryapp.backend.location.service;

import java.util.List;

import com.pe.inventoryapp.backend.location.model.request.RegionRequest;
import com.pe.inventoryapp.backend.location.model.response.RegionResponse;

public interface RegionService {
  void saveRegion(RegionRequest regionRequest);

  List<RegionResponse> findAllRegions();

  RegionResponse findRegionById(Long id);

  void updateRegionById(Long id, RegionRequest regionRequest);

  List<RegionResponse> findAllRegionsByDeliveryOrder(Long deliveryOrderId);
}
