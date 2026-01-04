package com.pe.inventoryapp.backend.deliveryline.controller;

import java.time.LocalDateTime;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.deliveryline.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineUpdateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineListResponse;
import com.pe.inventoryapp.backend.deliveryline.service.DeliveryLineService;
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
  @PostMapping("/product-delivery-order/{idProduct_DeliveryOrder}")
  public ResponseEntity<CommonResponse> registerDeliveryLine(
      Authentication authentication,
      @PathVariable Long idProduct_DeliveryOrder,
      @Valid @RequestBody DeliveryLineRequest deliveryOrderRequest,
      BindingResult result) {

        Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);

    validationService.validateFieldsAndThrowResponse(result);
    deliveryLineService.saveDeliveryLine(deliveryOrderRequest,idProduct_DeliveryOrder, id_user);

    return ResponseEntity.status(201).body(responseService.generateCommonResponse("success",
        ResponseStatus.SUCCESS,
        "Nueva orden pendiente"));
  }


  @GetMapping("/delivery-order/{id}")
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
  public ResponseEntity<?> getDeliveryLine(@PathVariable Long id) {
    DeliveryLineDetailsResponse deliveryLine = deliveryLineService.findDeliveryLineById(id);
    return ResponseEntity.status(200).body(deliveryLine);
  }

  @PutMapping("/{id}")
   public ResponseEntity<?> updateDeliveryLine(Authentication authentication, @PathVariable Long id, @Valid @RequestBody DeliveryLineUpdateRequest deliveryLineUpdateRequest,
      BindingResult result) {
     Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);

    validationService.validateFieldsAndThrowResponse(result);

    deliveryLineService.updateDeliveryLineById(id, deliveryLineUpdateRequest, id_user);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatus.SUCCESS,
        "Se actualizo la linea de entrega"));
  }

  // ACTUALIZAR EL ESTADO DE LA LINEA DE ENTREGA SI FUE ENTREGADO
  // Solamente si tiene el estado READY
  @PatchMapping("/{id}/delivered")
  public ResponseEntity<CommonResponse> changeDeliveredStatusDeliveryLine(Authentication authentication,
      @PathVariable Long id) {

    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    deliveryLineService.changeDeliveredStatusDeliveryLineById(id, id_user);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatus.SUCCESS,
        "La linea de entrega tiene el estado entregado"));
  }


  // ACTUALIZAR EL ESTADO DE LA LINEA DE ENTREGA SI FUE CANCELADO
  // Solamente si tiene los estados INPROGRESS o READY
  @PatchMapping("/{id}/canceled")
  public ResponseEntity<CommonResponse> changeCanceledStatusDeliveryLine(Authentication authentication,
      @PathVariable Long id) {

    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    deliveryLineService.changeCanceledStatusDeliveryLineById(id, id_user);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatus.SUCCESS,
        "La linea de entrega tiene el estado cancelado"));
  }

  // ACTUALIZAR EL ESTADO DE LA LINEA DE ENTREGA SI FUE PERDIDO (TENDRA EL DERECHO DE REPONER EL PEDIDO EXTRAVIADO)
  // Solamente si tiene el estado DELIVERED
  @PatchMapping("/{id}/missing")
  public ResponseEntity<CommonResponse> changeMissingStatusDeliveryLine(Authentication authentication,
      @PathVariable Long id) {

    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    deliveryLineService.changeMissingStatusDeliveryLineById(id, id_user);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatus.SUCCESS,
        "La linea de entrega tiene el estado perdido"));
  }

  // RECORDAR QUE SOLAMENTE PODRA BORRAR UNA LINEA DE ENTREGA SI NO HAY CANTIDAD ENTREGADA
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteDeliveryLine(@PathVariable Long id) {
    deliveryLineService.deleteDeliveryLineById(id);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatus.SUCCESS,
        "Se elimino la linea de entrega"));
  }
}
