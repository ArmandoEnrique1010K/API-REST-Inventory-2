package com.pe.inventoryapp.backend.deliveryorder.controller;

import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderComentRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientListResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.deliveryorder.service.DeliveryOrderService;
import com.pe.inventoryapp.backend.user.model.entity.UserPrincipal;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/delivery-orders")
public class DeliveryOrderController {

  private final ResponseService responseService;
  private final ValidationService validationService;
  private final DeliveryOrderService deliveryOrderService;

  public DeliveryOrderController(
      ResponseService responseService,
      ValidationService validationService,
      DeliveryOrderService deliveryOrderService) {
    this.responseService = responseService;
    this.validationService = validationService;
    this.deliveryOrderService = deliveryOrderService;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<CommonResponse> registerDeliveryOrder(Authentication authentication,
      @Valid @RequestBody DeliveryOrderRequest deliveryOrderRequest,
      BindingResult result) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    validationService.validateFieldsAndThrowResponse(result);
    deliveryOrderService.saveDeliveryOrder(deliveryOrderRequest, userPrincipal.getId());

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se ha creado la orden de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<?> listAllDeliveryOrders(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String batch,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      @RequestParam(required = false) String userClientName,
      @RequestParam(required = false) OrderStatus status) {
    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<DeliveryOrderListResponse> deliveryOrders = deliveryOrderService.findAllDeliveryOrdersByParams(
        pageable, batch,
        startDate, endDate, status, userClientName);

    DataResponse<PageResponse<DeliveryOrderListResponse>> dataResponse = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        deliveryOrders);

    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @PreAuthorize("hasRole('OPERATOR')")
  @GetMapping("/in-progress")
  public ResponseEntity<?> listAllPendingDeliveryOrders(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String batch,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      @RequestParam(required = false) String userClientName) {

    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<DeliveryOrderListResponse> deliveryOrders = deliveryOrderService.findAllActiveDeliveryOrdersByParams(
        pageable, batch, startDate, endDate, userClientName);

    DataResponse<PageResponse<DeliveryOrderListResponse>> dataResponse = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        deliveryOrders);

    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  // Este endpoint se utiliza para listar todas las ordenes de entrega por el
  // usuario que ha iniciado sesión
  @GetMapping("/client")
  public ResponseEntity<?> listAllDeliveryOrdersByClient(Authentication authentication,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String batch,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      @RequestParam(required = false) OrderStatus status) {

    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<DeliveryOrderClientListResponse> deliveryOrders = deliveryOrderService
        .findAllDeliveryOrderByClientId(pageable, userPrincipal.getId(), batch, startDate, endDate, status);

    DataResponse<PageResponse<DeliveryOrderClientListResponse>> dataResponse = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        deliveryOrders);

    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  // Obtiene una orden de entrega por id
  @PreAuthorize("hasRole('OPERATOR')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getDeliveryOrder(@PathVariable Long id) {
    DeliveryOrderDetailsResponse deliveryOrderDetailsResponse = deliveryOrderService.findDeliveryOrderById(id);
    DataResponse<DeliveryOrderDetailsResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        deliveryOrderDetailsResponse);
    return ResponseEntity.status(response.status()).body(response);
  }

  // Endpoint para obtener una orden de entrega para un cliente
  @GetMapping("/{id}/client")
  public ResponseEntity<?> getDeliveryOrderForClient(Authentication authentication, @PathVariable Long id) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    DeliveryOrderClientDetailsResponse deliveryOrderDetailsResponse = deliveryOrderService
        .findDeliveryOrderByIdAndValidateUserClient(id, userPrincipal.getId());

    DataResponse<DeliveryOrderClientDetailsResponse> response = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        deliveryOrderDetailsResponse);

    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}")
  public ResponseEntity<?> changeLimitDateDeliveryOrder(Authentication authentication, @PathVariable Long id,
      @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime limitDate) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    deliveryOrderService.changeLimitDate(id, limitDate, userPrincipal.getId());

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cambiado la fecha de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}/cancel")
  public ResponseEntity<CommonResponse> cancelDeliveryOrder(Authentication authentication, @PathVariable Long id,
      @Valid @RequestBody DeliveryOrderComentRequest deliveryOrderComentRequest, BindingResult result) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    validationService.validateFieldsAndThrowResponse(result);
    deliveryOrderService.processDeliveryOrderCancellation(id, deliveryOrderComentRequest, userPrincipal.getId());

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cancelado la orden de entrega, todos los modelos de los productos que aun no fuerón entregados serán devueltos inmediatamente");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/send")
  public ResponseEntity<CommonResponse> sendDeliveryOrder(Authentication authentication, @PathVariable Long id) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    deliveryOrderService.markDeliveryOrderAsDelivered(id, userPrincipal.getId());

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha enviado la orden de entrega al cliente");
    return ResponseEntity.status(response.status()).body(response);
  }
}
