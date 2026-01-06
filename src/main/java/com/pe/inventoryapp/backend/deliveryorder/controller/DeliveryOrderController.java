package com.pe.inventoryapp.backend.deliveryorder.controller;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.deliveryorder.service.DeliveryOrderService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/delivery-order")
public class DeliveryOrderController {

  @Autowired
  private ResponseService responseService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private DeliveryOrderService deliveryOrderService;


  @Autowired
  private AuthenticationContextService authenticationContextService;

  @PostMapping
  public ResponseEntity<CommonResponse> registerDeliveryOrder(Authentication authentication,
      @Valid @RequestBody DeliveryOrderRequest deliveryOrderRequest,
      BindingResult result) {
    Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);

    validationService.validateFieldsAndThrowResponse(result);
    deliveryOrderService.saveDeliveryOrder(deliveryOrderRequest, id);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatus.SUCCESS,
            "Se ha creado la orden de entrega"));
    }

  @GetMapping
  public ResponseEntity<?> listAllDeliveryOrder(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) OrderStatus orderStatus,
      @RequestParam(required = false) String createdByUser,
      @RequestParam(required = false) String batch,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<DeliveryOrderListResponse> deliveryOrders = deliveryOrderService.findAllDeliveryOrdersByParams(
        orderStatus, createdByUser, batch, startDate, endDate, pageable);

    return ResponseEntity.status(200).body(deliveryOrders);
  }


  @GetMapping("/pending")
  public ResponseEntity<?> listAllPendingDeliveryOrder(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String createdByUser,
      @RequestParam(required = false) String batch,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

    Pageable pageable = PageRequest.of(page, 20);

    Page<DeliveryOrderListResponse> deliveryOrders = deliveryOrderService.findAllActiveDeliveryOrdersByParams(
        createdByUser, batch, startDate, endDate, pageable);

    return ResponseEntity.status(200).body(deliveryOrders);
  }




  @GetMapping("/{id}")
  public ResponseEntity<?> getDeliveryOrder(@PathVariable Long id) {
    DeliveryOrderDetailsResponse deliveryOrderDetailsResponse = deliveryOrderService.findDeliveryOrderById(id);
    return ResponseEntity.status(200).body(deliveryOrderDetailsResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateDeliveryOrder(Authentication authentication, @PathVariable Long id,
      @Valid @RequestBody DeliveryOrderRequest deliveryOrderRequest, BindingResult result) {
            Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);

    validationService.validateFieldsAndThrowResponse(result);
    deliveryOrderService.updateDeliveryOrderById(id, deliveryOrderRequest, id_user);


    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatus.SUCCESS,
        "Se actualizo la orden de entrega"));

  }

  // @PatchMapping("/{id}/{preparationStatus}")
  // public ResponseEntity<CommonResponse> changePreparationStatusDeliveryOrder(Authentication authentication, @PathVariable Long id,
  //     @PathVariable PreparationStatus preparationStatus) {

  //       Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
  //   deliveryOrderService.changePreparationStatusDeliveryOrderById(id, preparationStatus, id_user);
  //   return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
  //       ResponseStatusCodes.SUCCESS,
  //       "Se ha cambiado el estado del pedido de entrega"));
  // }
}
