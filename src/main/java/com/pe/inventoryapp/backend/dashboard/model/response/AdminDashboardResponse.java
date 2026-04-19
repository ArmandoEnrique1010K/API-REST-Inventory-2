package com.pe.inventoryapp.backend.dashboard.model.response;

import java.util.List;

import com.pe.inventoryapp.backend.dashboard.model.dto.PendingDeliveryOrdersDto;
import com.pe.inventoryapp.backend.movement.model.dto.MovementDto;
import com.pe.inventoryapp.backend.product.model.dto.ModelDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private Long quantityDeliveryOrdersPending;
  private Long quantityModelsActive;
  private Long quantityLowStockModels;
  private Long quantityNearCaducityDateModels;
  private Long quantityMovementsToday;
  private List<PendingDeliveryOrdersDto> deliveryOrderSummaryDto;
  private List<ModelDto> lowStockModels;
  private List<ModelDto> recentModels;
  private List<ModelDto> expiringSoonModels;
  private List<MovementDto> recentMovements;
}
