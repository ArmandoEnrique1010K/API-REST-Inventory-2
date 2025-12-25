package com.pe.inventoryapp.backend.delivery.controller;

import java.time.LocalDate;
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

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineListResponse;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.delivery.service.DeliveryLineService;
import com.pe.inventoryapp.backend.delivery.service.DeliveryOrderService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
  private DeliveryLineService deliveryLineService;

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
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha creado la orden de entrega"));
    }

    // TODO: USAR AUTHENTICATIONCONTEXTSERVICEIMPL
  //   Long id_user = jwtService.extractUserIdFromClaims(header);

  //   if (id_user == null) {
  //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
  //         .body(responseService.generateCommonResponse("error", "El usuario no ha iniciado sesión"));
  //   }

  //   deliveryOrderService.verifyBatchExist(deliveryOrderRequest.getBatch());

  //   var deliveryOrder = deliveryOrderService.save(deliveryOrderRequest, id_user);

  //   if (deliveryOrder == null) {
  //     return ResponseEntity.status(HttpStatus.CREATED)
  //         .body(responseService.generateCommonResponse("error", "Error al registrar el pedido de entrega"));
  //   }
  //   return ResponseEntity.status(HttpStatus.CREATED)
  //       .body(responseService.generateCommonResponse("success", deliveryOrder));
  // }

  @GetMapping
  public ResponseEntity<?> listAllDeliveryOrder(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) PreparationStatus preparationStatus,
      @RequestParam(required = false) String createdByUser,
      @RequestParam(required = false) String batch,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<DeliveryOrderListResponse> deliveryOrders = deliveryOrderService.findAllDeliveryOrdersByParams(preparationStatus, createdByUser, batch, startDate, endDate, pageable);


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

  // @GetMapping("/search")
  // public Page<DeliveryOrderListResponse> findAllByParams(@RequestParam(defaultValue = "0") Integer page,
  //     @RequestParam(required = false) PreparationStatus status,
  //     @RequestParam(required = false) String createdByUser,
  //     @RequestParam(required = false) String batch,
  //     @RequestParam(required = false) Integer minQuantity,
  //     @RequestParam(required = false) Integer maxQuantity,
  //     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
  //     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

  //   Pageable pageable = PageRequest.of(page, 10);

  //   return deliveryOrderService.findAllDeliveryOrdersByParams(pageable, status, createdByUser, batch, minQuantity,
  //       maxQuantity, startDate, endDate);
  // }


  @GetMapping("/{id}/delivery-lines")
  public ResponseEntity<?> listAllDeliveryLinesByDeliveryOrder(
      @PathVariable Long id, 
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) Integer minRequiredQuantity,
      @RequestParam(required = false) Integer maxRequiredQuantity,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime minLimitDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime maxLimitDate,
      @RequestParam(required = false) PreparationStatus preparationStatus,
      @RequestParam(required = false) String location    
    ) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<DeliveryLineListResponse> deliveryOrder = deliveryLineService.findAllDeliveryLinesByDeliveryOrderIdPageable(id, 
        minRequiredQuantity, maxRequiredQuantity, minLimitDate, maxLimitDate, preparationStatus, location, pageable);

    return ResponseEntity.status(200).body(deliveryOrder);
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

    // String newBatch = deliveryOrderRequest.getBatch();

    // if (optionalDeliveryOrderResponse.isEmpty()) {
    //   return ResponseEntity.status(400).body(responseService.generateCommonResponse("error", "La orden no existe"));
    // }

    // if (!optionalDeliveryOrderResponse.get().getBatch().equals(newBatch)) {
    //   deliveryOrderService.verifyBatchExist(newBatch);
    // }

    // String message = deliveryOrderService.update(id, deliveryOrderRequest);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatusCodes.SUCCESS_RESPONSE,
        "Se actualizo la orden de entrega"));

  }

  @PatchMapping("/{preparationStatus}/{id}")
  public ResponseEntity<CommonResponse> changePreparationStatusDeliveryOrder(Authentication authentication, @PathVariable Long id,
      @PathVariable PreparationStatus preparationStatus) {

        Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    deliveryOrderService.changePreparationStatusDeliveryOrderById(id, preparationStatus, id_user);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatusCodes.SUCCESS_RESPONSE,
        "Se ha cambiado el estado del pedido de entrega"));
  }

}
