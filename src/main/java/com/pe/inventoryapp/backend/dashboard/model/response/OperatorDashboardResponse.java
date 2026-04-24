package com.pe.inventoryapp.backend.dashboard.model.response;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderSummaryResponse;
import com.pe.inventoryapp.backend.product.model.dto.ModelDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatorDashboardResponse {
  private String userFullname;
  private Long quantityDeliveryOrdersPending;
  private Long quantityModelsActive;
  private Long quantityLowStockModels;
  private Long quantityNearCaducityDateModels;
  private List<DeliveryOrderSummaryResponse> pendingDeliveryOrders;
  private List<ModelDto> lowStockModels;
  private List<ModelDto> recentModels;
  private List<ModelDto> expiringSoonModels;
}
