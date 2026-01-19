package com.pe.inventoryapp.backend.deliveryorder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.deliveryorder.model.response.Product_DeliveryOrder_RegionResponse;
import com.pe.inventoryapp.backend.deliveryorder.service.Product_DeliveryOrder_RegionService;

@RestController
@RequestMapping("/api/product-delivery-order-region")
public class Product_DeliveryOrder_RegionController {

  @Autowired
  private Product_DeliveryOrder_RegionService Product_DeliveryOrder_RegionService;

  @Autowired
  private ResponseService responseService;

  @GetMapping("/delivery-order/{deliveryOrderId}")
  public ResponseEntity<?> getSummaryByDeliveryOrder(@PathVariable Long deliveryOrderId) {

    List<Product_DeliveryOrder_RegionResponse> product_DeliveryOrder_RegionResponses = Product_DeliveryOrder_RegionService.findAllByDeliveryOrderId(deliveryOrderId);

        DataResponse<List<Product_DeliveryOrder_RegionResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS,
            product_DeliveryOrder_RegionResponses);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);

  }
  
}
