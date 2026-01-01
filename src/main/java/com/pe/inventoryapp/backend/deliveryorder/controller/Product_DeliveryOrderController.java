package com.pe.inventoryapp.backend.deliveryorder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.deliveryorder.model.request.Product_DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.service.Product_DeliveryOrderService;
import com.pe.inventoryapp.backend.product.model.request.ProductRequest;
import com.pe.inventoryapp.backend.product.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/product-delivery-order")
public class Product_DeliveryOrderController {
  @Autowired
  private Product_DeliveryOrderService product_DeliveryOrderService;

  @Autowired
  private ResponseService responseService;

  @Autowired
  private ValidationService validationService;

  @PostMapping("/{idDeliveryOrder}")
  public ResponseEntity<CommonResponse> relationManyProductsToDeliveryOrder(@PathVariable Long idDeliveryOrder, @Valid @RequestBody Product_DeliveryOrderRequest product_DeliveryOrderRequest, BindingResult result) {

    validationService.validateFieldsAndThrowResponse(result);
    product_DeliveryOrderService.saveProduct_DeliveryOrder(product_DeliveryOrderRequest, idDeliveryOrder);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se han agregado productos a la orden de entrega"));
  }
}
