package com.pe.inventoryapp.backend.delivery.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.delivery.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineResponse;
import com.pe.inventoryapp.backend.delivery.service.DeliveryLineService;
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

  @PostMapping
  public ResponseEntity<CommonResponse> save(
      @Valid @RequestBody DeliveryLineRequest deliveryOrderRequest,
      BindingResult result) {

    validationService.validateFieldsAndThrowResponse(result);

    deliveryLineService.save(deliveryOrderRequest);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", "Nueva orden pendiente"));
  }

  @GetMapping("/delivery-order/{id}")
  public ResponseEntity<?> findAllByIdDeliveryOrderPageable(
      @PathVariable Long id, @RequestParam(defaultValue = "0") Integer page) {

    // Buscar el id corresponiente
    Optional<DeliveryLineResponse> deliveryOrder = deliveryLineService.findById(id);

    // Este es el metodo correcto
    if (deliveryOrder.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(responseService.generateCommonResponse(
              "error",
              "No se ha encontrado la orden de entrega"));
    }

    Pageable pageable = PageRequest.of(0, 20);
    return ResponseEntity.status(HttpStatus.OK)
        .body(deliveryLineService.findAllByIdDeliveryOrderPageable(id, pageable));
  }

}
