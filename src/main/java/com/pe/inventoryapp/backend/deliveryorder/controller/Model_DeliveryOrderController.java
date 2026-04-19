package com.pe.inventoryapp.backend.deliveryorder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.deliveryorder.model.response.Model_DeliveryOrderResponse;
import com.pe.inventoryapp.backend.deliveryorder.service.Model_DeliveryOrderService;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/api/models-delivery-orders")
public class Model_DeliveryOrderController {
  private final Model_DeliveryOrderService model_DeliveryOrderService;
  private final ResponseService responseService;

  public Model_DeliveryOrderController(Model_DeliveryOrderService model_DeliveryOrderService,
      ResponseService responseService) {
    this.model_DeliveryOrderService = model_DeliveryOrderService;
    this.responseService = responseService;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/model/{modelId}/deliveryOrder/{deliveryOrderId}")
  public ResponseEntity<CommonResponse> registerRelationModelToDeliveryOrder(@PathVariable Long modelId,
      @PathVariable Long deliveryOrderId) {
    model_DeliveryOrderService.saveRelationModelInDeliveryOrder(modelId, deliveryOrderId);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se ha agregado un modelo del producto a la orden de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping("/models/deliveryOrder/{deliveryOrderId}")
  public ResponseEntity<?> listAllModelsByDeliveryOrder(@PathVariable Long deliveryOrderId) {
    List<Model_DeliveryOrderResponse> model_DeliveryOrderListResponses = model_DeliveryOrderService
        .findAllByDeliveryOrderId(
            deliveryOrderId);

    DataResponse<List<Model_DeliveryOrderResponse>> dataResponse = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        model_DeliveryOrderListResponses);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}")
  public ResponseEntity<CommonResponse> inactiveRelationModelToDeliveryOrder(@PathVariable Long id) {
    model_DeliveryOrderService.deleteRelationModelDeliveryOrder(id);
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha eliminado la relación de un modelo - orden de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }
}
