package com.pe.inventoryapp.backend.summary.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.summary.model.response.Model_DeliveryOrder_RegionResponse;
import com.pe.inventoryapp.backend.summary.service.Model_DeliveryOrder_RegionService;

@RestController
@RequestMapping("/api/model-delivery-order-region")
public class Model_DeliveryOrder_RegionController {
  private final Model_DeliveryOrder_RegionService model_DeliveryOrder_RegionService;
  private final ResponseService responseService;

  public Model_DeliveryOrder_RegionController (
      Model_DeliveryOrder_RegionService model_DeliveryOrder_RegionService,
      ResponseService responseService
  ){
    this.model_DeliveryOrder_RegionService = model_DeliveryOrder_RegionService;
    this.responseService = responseService;
  }

  @GetMapping("/delivery-order/{deliveryOrderId}")
  public ResponseEntity<?> getSummaryByDeliveryOrder(@PathVariable Long deliveryOrderId) {

    List<Model_DeliveryOrder_RegionResponse> product_DeliveryOrder_RegionResponses = model_DeliveryOrder_RegionService.findAllByDeliveryOrderId(deliveryOrderId);

        DataResponse<List<Model_DeliveryOrder_RegionResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS,
            product_DeliveryOrder_RegionResponses);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }
}
