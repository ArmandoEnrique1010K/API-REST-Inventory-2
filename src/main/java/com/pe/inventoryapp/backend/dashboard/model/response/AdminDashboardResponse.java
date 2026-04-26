package com.pe.inventoryapp.backend.dashboard.model.response;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderSummaryResponse;
import com.pe.inventoryapp.backend.movement.model.response.MovementsTodaySummaryResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelExpiringSoonSummaryResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelLowStockSummaryResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelRecentsSummaryResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
  private String userFullname;
  private Long quantityDeliveryOrdersPending;
  private Long quantityModelsActive;
  private Long quantityLowStockModels;
  private Long quantityNearCaducityDateModels;
  private Long quantityMovementsToday;
  private List<DeliveryOrderSummaryResponse> pendingDeliveryOrders;
  private List<ModelLowStockSummaryResponse> lowStockModels;
  private List<ModelRecentsSummaryResponse> recentModels;
  private List<ModelExpiringSoonSummaryResponse> expiringSoonModels;
  private List<MovementsTodaySummaryResponse> recentMovements;
}
