package com.pe.inventoryapp.backend.summary.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOrderSummaryResponse {
  private Long deliveryOrderId;
  private List<RegionSummaryResponse> regions;
}
