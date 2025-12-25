package com.pe.inventoryapp.backend.delivery.controller;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineListResponse;
import com.pe.inventoryapp.backend.delivery.service.DeliveryLineService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/delivery-line")
public class DeliveryLineController {
  @Autowired
  private ResponseService responseService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private DeliveryLineService deliveryLineService;

  @Autowired
  private AuthenticationContextService authenticationContextService;
  @PostMapping("/delivery-order")
  public ResponseEntity<CommonResponse> registerDeliveryLine(
      Authentication authentication,
      @Valid @RequestBody DeliveryLineRequest deliveryOrderRequest,
      BindingResult result) {

        Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);

    validationService.validateFieldsAndThrowResponse(result);
    deliveryLineService.saveDeliveryLine(deliveryOrderRequest, id_user);

    return ResponseEntity.status(201).body(responseService.generateCommonResponse("success",
        ResponseStatusCodes.SUCCESS_RESPONSE,
        "Nueva orden pendiente"));
  }

  @GetMapping("/delivery-order/{id}")
  public ResponseEntity<?> listAllDeliveryLinesByDeliveryOrder(
      @PathVariable Long id, 
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) Integer minRequiredQuantity,
      @RequestParam(required = false) Integer maxRequiredQuantity,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate minLimitDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate maxLimitDate,
      @RequestParam(required = false) PreparationStatus preparationStatus,
      @RequestParam(required = false) String location    
    ) {
    Pageable pageable = PageRequest.of(0, 20);

    Page<DeliveryLineListResponse> deliveryOrder = deliveryLineService.findAllDeliveryLinesByDeliveryOrderIdPageable(id, 
        minRequiredQuantity, maxRequiredQuantity, minLimitDate, maxLimitDate, preparationStatus, location, pageable);

    return ResponseEntity.status(200).body(deliveryOrder);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getDeliveryLine(@PathVariable Long id) {
    DeliveryLineDetailsResponse deliveryLine = deliveryLineService.findDeliveryLineById(id);
    return ResponseEntity.status(200).body(deliveryLine);
  }

  @PutMapping("/{id}")
   public ResponseEntity<?> updateDeliveryLine(Authentication authentication, @PathVariable Long id, @Valid @RequestBody DeliveryLineRequest deliveryLineRequest,
      BindingResult result) {
     Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);

    validationService.validateFieldsAndThrowResponse(result);

    deliveryLineService.updateDeliveryLineById(id, deliveryLineRequest, id_user);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatusCodes.SUCCESS_RESPONSE,
        "Se actualizo la linea de entrega"));
  }
 
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteDeliveryLine(@PathVariable Long id) {
    deliveryLineService.deleteDeliveryLineById(id);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatusCodes.SUCCESS_RESPONSE,
        "Se elimino la linea de entrega"));
  }
}
