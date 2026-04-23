package com.pe.inventoryapp.backend.dashboard.service;

import com.pe.inventoryapp.backend.dashboard.model.response.AdminDashboardResponse;
import com.pe.inventoryapp.backend.dashboard.model.response.OperatorDashboardResponse;
import com.pe.inventoryapp.backend.dashboard.model.response.UserDashboardResponse;

public interface DashboardService {
  UserDashboardResponse getSummaryByRoleUser(Long idUser);

  OperatorDashboardResponse getSummaryByRoleOperator(Long idUser);

  AdminDashboardResponse getSummaryByRoleAdmin(Long idUser);
}
