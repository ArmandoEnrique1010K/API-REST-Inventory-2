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
import com.pe.inventoryapp.backend.summary.model.response.Model_DeliveryOrder_SubregionResponse;
import com.pe.inventoryapp.backend.summary.service.Model_DeliveryOrder_SubregionService;

@RestController
@RequestMapping("/api/model-delivery-order-subregion")
public class Model_DeliveryOrder_SubregionController {
  private final Model_DeliveryOrder_SubregionService model_DeliveryOrder_SubregionService;
  private final ResponseService responseService;

  public Model_DeliveryOrder_SubregionController(
      Model_DeliveryOrder_SubregionService model_DeliveryOrder_SubregionService,
      ResponseService responseService) {
    this.model_DeliveryOrder_SubregionService = model_DeliveryOrder_SubregionService;
    this.responseService = responseService;
  }

  @GetMapping("/delivery-order/{deliveryOrderId}")
  public ResponseEntity<?> getSummaryByDeliveryOrder(@PathVariable Long deliveryOrderId) {

    List<Model_DeliveryOrder_SubregionResponse> product_DeliveryOrder_SubregionResponses = model_DeliveryOrder_SubregionService
        .findAllByDeliveryOrderId(deliveryOrderId);

    DataResponse<List<Model_DeliveryOrder_SubregionResponse>> dataResponse = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        product_DeliveryOrder_SubregionResponses);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }
}
