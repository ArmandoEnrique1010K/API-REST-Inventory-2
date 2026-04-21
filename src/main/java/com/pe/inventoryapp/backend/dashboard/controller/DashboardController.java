package com.pe.inventoryapp.backend.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.dashboard.model.response.AdminDashboardResponse;
import com.pe.inventoryapp.backend.dashboard.model.response.OperatorDashboardResponse;
import com.pe.inventoryapp.backend.dashboard.model.response.UserDashboardResponse;
import com.pe.inventoryapp.backend.dashboard.service.DashboardService;
import com.pe.inventoryapp.backend.user.model.entity.UserPrincipal;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final ResponseService responseService;
  private final DashboardService dashboardService;
  public DashboardController(
    ResponseService responseService,
      DashboardService dashboardService
  ){
    this.responseService = responseService;
    this.dashboardService = dashboardService;
  }

  //* EN ESTE CASO SE QUIERE IGNORAR LA JERARQUIA DE ROLES Y QUE SOLAMENTE EL USUARIO CON ESE ROL PUEDA ACCEDER AL ENDPOINT
  // @PreAuthorize("hasRole('USER')")
  @PreAuthorize("principal.role == 'ROLE_USER'")
  @GetMapping("/user")
  public ResponseEntity<?> getDashboardUser(Authentication authentication){
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    UserDashboardResponse userDashboardResponse = dashboardService.getSummaryByRoleUser(userPrincipal.getId());
    DataResponse<UserDashboardResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS, userDashboardResponse);
    return ResponseEntity.status(response.status()).body(response);
  }
  

  @PreAuthorize("principal.role == 'ROLE_OPERATOR'")
  @GetMapping("/operator")
  public ResponseEntity<?> getDashboardOperator(Authentication authentication) {
    OperatorDashboardResponse operatorDashboardResponse = dashboardService.getSummaryByRoleOperator();
    DataResponse<OperatorDashboardResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        operatorDashboardResponse);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("principal.role == 'ROLE_ADMIN'")
  @GetMapping("/admin")
  public ResponseEntity<?> getDashboardAdmin(Authentication authentication) {
    AdminDashboardResponse adminDashboardResponse = dashboardService.getSummaryByRoleAdmin();
    DataResponse<AdminDashboardResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        adminDashboardResponse);
    return ResponseEntity.status(response.status()).body(response);
  }
}
