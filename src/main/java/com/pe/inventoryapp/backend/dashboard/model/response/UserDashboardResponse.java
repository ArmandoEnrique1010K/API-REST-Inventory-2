package com.pe.inventoryapp.backend.dashboard.model.response;


import java.util.List;

import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderSummaryByClientResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardResponse {
  private String userFullname;
  private Long pendingDeliveryOrdersByUserCount;
  private List<DeliveryOrderSummaryByClientResponse> pendingDeliveryOrdersByUser;
}
