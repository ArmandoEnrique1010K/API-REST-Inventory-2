package com.pe.inventoryapp.backend.summary.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.summary.model.response.DeliveryOrderSummaryResponse;
import com.pe.inventoryapp.backend.summary.service.DeliveryOrderSummaryDomainService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/delivery-order-summary")
public class DeliveryOrderSummaryController {

  private final DeliveryOrderSummaryDomainService deliveryOrderSummaryDomainService;
  private final ResponseService responseService;

  public DeliveryOrderSummaryController(DeliveryOrderSummaryDomainService deliveryOrderSummaryDomainService,
      ResponseService responseService) {
    this.deliveryOrderSummaryDomainService = deliveryOrderSummaryDomainService;
    this.responseService = responseService;
  }

  @GetMapping("/delivery-order/{deliveryOrderId}")
  public ResponseEntity<?> getSummaryByDeliveryOrder(@PathVariable Long deliveryOrderId) {
    DeliveryOrderSummaryResponse deliveryOrderSummaryResponse = deliveryOrderSummaryDomainService
        .getSummary(deliveryOrderId);

    DataResponse<DeliveryOrderSummaryResponse> dataResponse = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        deliveryOrderSummaryResponse);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

}
