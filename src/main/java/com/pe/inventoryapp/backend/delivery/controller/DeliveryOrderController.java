package com.pe.inventoryapp.backend.delivery.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.JwtService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.delivery.service.DeliveryOrderService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

  // TODO: DESACTIVAR EL MODO REQUIRED FALSE, SOLAMENTE ES PARA PRUEBAS
  @PostMapping
  public ResponseEntity<CommonResponse> save(@RequestHeader(value = "Authorization", required = false) String header,
      @Valid @RequestBody DeliveryOrderRequest deliveryOrderRequest,
      BindingResult result) {

    if (header == null || header.isBlank()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(responseService.generateCommonResponse("error", "Falta el token de autorización"));
    }

    // TODO: USAR AUTHENTICATIONCONTEXTSERVICEIMPL
    Long id_user = jwtService.extractUserIdFromClaims(header);

    if (id_user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(responseService.generateCommonResponse("error", "El usuario no ha iniciado sesión"));
    }

    // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar
    // el pedido de entrega");

    validationService.validateFieldsAndThrowResponse(result);
    deliveryOrderService.verifyBatchExist(deliveryOrderRequest.getBatch());

    var deliveryOrder = deliveryOrderService.save(deliveryOrderRequest, id_user);

    if (deliveryOrder == null) {
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(responseService.generateCommonResponse("error", "Error al registrar el pedido de entrega"));
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", deliveryOrder));
  }

  @GetMapping("/search")
  public Page<DeliveryOrderListResponse> findAllByParams(@RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) PreparationStatus status,
      @RequestParam(required = false) String createdByUser,
      @RequestParam(required = false) String batch,
      @RequestParam(required = false) Integer minQuantity,
      @RequestParam(required = false) Integer maxQuantity,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

    Pageable pageable = PageRequest.of(page, 10);

    return deliveryOrderService.findAllDeliveryOrdersByParams(pageable, status, createdByUser, batch, minQuantity,
        maxQuantity, startDate, endDate);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findById(@PathVariable Long id) {
    Optional<DeliveryOrderDetailsResponse> deliveryOrder = deliveryOrderService.findById(id);
    if (!deliveryOrder.isPresent()) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "No se ha encontrado la orden de entrega"));
    }

    return ResponseEntity.status(200).body(deliveryOrder);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id,
      @Valid @RequestBody DeliveryOrderRequest deliveryOrderRequest, BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    Optional<DeliveryOrderDetailsResponse> optionalDeliveryOrderResponse = deliveryOrderService.findById(id);

    String newBatch = deliveryOrderRequest.getBatch();

    if (optionalDeliveryOrderResponse.isEmpty()) {
      return ResponseEntity.status(400).body(responseService.generateCommonResponse("error", "La orden no existe"));
    }

    if (!optionalDeliveryOrderResponse.get().getBatch().equals(newBatch)) {
      deliveryOrderService.verifyBatchExist(newBatch);
    }

    String message = deliveryOrderService.update(id, deliveryOrderRequest);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success", message));

  }

  @PatchMapping("/{id}/{preparationStatus}")
  public ResponseEntity<CommonResponse> changePreparationStatus(@PathVariable Long id,
      @PathVariable PreparationStatus preparationStatus) {
    deliveryOrderService.changePreparationStatus(id, preparationStatus);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(responseService.generateCommonResponse("success", "Se ha cambiado el estado del pedido de entrega"));
  }

}
