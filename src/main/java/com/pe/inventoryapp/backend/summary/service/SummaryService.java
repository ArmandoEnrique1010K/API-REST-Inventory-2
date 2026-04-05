package com.pe.inventoryapp.backend.summary.service;

import java.util.List;

import com.pe.inventoryapp.backend.summary.model.response.RegionSummaryDTO;

public interface SummaryService {
  List<RegionSummaryDTO> buildSummary(Long deliveryOrderId);
}
