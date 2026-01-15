package com.pe.inventoryapp.backend.deliveryorder.controller;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientListResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.deliveryorder.service.DeliveryOrderService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/delivery-orders")
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
    Long id_user_authenticated = authenticationContextService.extractUserIdFromAuthentication(authentication);

    validationService.validateFieldsAndThrowResponse(result);
    deliveryOrderService.saveDeliveryOrder(deliveryOrderRequest, id_user_authenticated);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se ha creado la orden de entrega");
    return ResponseEntity.status(response.status()).body(response);
    }

  @GetMapping
  public ResponseEntity<?> listAllDeliveryOrders(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String batch,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      @RequestParam(required = false) String userClientName,
      @RequestParam(required = false) OrderStatus status
    ) {
    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<DeliveryOrderListResponse> deliveryOrders = deliveryOrderService.findAllDeliveryOrdersByParams(batch, startDate, endDate, userClientName, status, pageable);

    return ResponseEntity.status(200).body(deliveryOrders);
  }

  @GetMapping("/in-progress")
  public ResponseEntity<?> listAllPendingDeliveryOrders(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String batch,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      @RequestParam(required = false) String userClientName
    ) {

    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<DeliveryOrderListResponse> deliveryOrders = deliveryOrderService.findAllActiveDeliveryOrdersByParams(batch,
        startDate, endDate, userClientName,  pageable);

    return ResponseEntity.status(200).body(deliveryOrders);
  }

  @GetMapping("/client")
  public ResponseEntity<?> listAllDeliveryOrdersByClient(Authentication authentication,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String batch,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      @RequestParam(required = false) String userClientName,
      @RequestParam(required = false) OrderStatus status
    ) {

    Long id_user_authenticated = authenticationContextService.extractUserIdFromAuthentication(authentication);

    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<DeliveryOrderClientListResponse> deliveryOrders = deliveryOrderService.findAllDeliveryOrdesByClientId(id_user_authenticated, batch, startDate, endDate, status, pageable);

    return ResponseEntity.status(200).body(deliveryOrders);
  }

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
    Long id_user_authenticated = authenticationContextService.extractUserIdFromAuthentication(authentication);

    DeliveryOrderClientDetailsResponse deliveryOrderDetailsResponse = deliveryOrderService.findDeliveryOrderByIdAndValidateUserClient(id, id_user_authenticated);
    DataResponse<DeliveryOrderClientDetailsResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS, 
        deliveryOrderDetailsResponse);
    return ResponseEntity.status(response.status()).body(response);
  }

  




  @PatchMapping("/{id}")
  public ResponseEntity<?> changeLimitDateDeliveryOrder(Authentication authentication, @PathVariable Long id, 
    //@Valid @RequestBody DeliveryOrderRequest deliveryOrderRequest, BindingResult result
    @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime limitDate
    ) {
    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);

    deliveryOrderService.changeLimitDate(id, limitDate, id_user);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cambiado la fecha de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }

  // @PatchMapping("/{id}/canceled")
  // public ResponseEntity<?> cancelDeliveryOrder(Authentication authentication, @PathVariable Long id) {
  //   Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);

  //   deliveryOrderService.changeStatusOrderToCanceledById(id, id_user);

  //   CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
  //       "Se ha cancelado la orden de entrega");
  //   return ResponseEntity.status(response.status()).body(response);
  // }


  @PatchMapping("/{id}/cancel")
  public ResponseEntity<CommonResponse> cancelDeliveryOrder(@PathVariable Long id) {
    deliveryOrderService.cancelDeliveryOrderById(id);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cancelado la orden de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }

}
