package com.pe.inventoryapp.backend.deliveryline.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
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
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineAllocateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineAlterRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineUpdateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineListResponse;
import com.pe.inventoryapp.backend.deliveryline.service.DeliveryLineService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/delivery-lines")
public class DeliveryLineController {
  @Autowired
  private ResponseService responseService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private DeliveryLineService deliveryLineService;

  @Autowired
  private AuthenticationContextService authenticationContextService;
  // TODO: ESTO SE PODRIA TRASLADAR HACIA EL CONTROLADOR DE PRODUCT-DELIVERY-ORDER
  @PostMapping("/product-delivery-order/{productDeliveryOrderId}")
  public ResponseEntity<CommonResponse> registerDeliveryLine(
      Authentication authentication,
      @PathVariable Long productDeliveryOrderId,
      @Valid @RequestBody DeliveryLineRequest deliveryLineRequest,
      BindingResult result) {

    Long id_user_authenticated = authenticationContextService.extractUserIdFromAuthentication(authentication);

    validationService.validateFieldsAndThrowResponse(result);
    deliveryLineService.saveDeliveryLine(deliveryLineRequest, productDeliveryOrderId, id_user_authenticated);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro una linea de entrega en una orden de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }

  

  // TODO: PODRIA AÑADIR UN PARAMETRO PARA LISTAR POR PRODUCTOS
  @GetMapping("/delivery-order/{productDeliveryOrderId}")
  public ResponseEntity<?> listAllDeliveryLinesByDeliveryOrder(
      @PathVariable Long productDeliveryOrderId, 
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) Integer minRequiredQuantity,
      @RequestParam(required = false) Integer maxRequiredQuantity,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime minLimitDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime maxLimitDate,
      @RequestParam(required = false) LineStatus lineStatus,
      @RequestParam(required = false) String location    
    ) {
    Pageable pageable = PageRequest.of(page, 20);
    PageResponse<DeliveryLineListResponse> deliveryOrders = deliveryLineService
        .findAllDeliveryLinesByDeliveryOrderIdPageable(
            productDeliveryOrderId,
            minRequiredQuantity, maxRequiredQuantity, minLimitDate, maxLimitDate, lineStatus, location, pageable);
    DataResponse<PageResponse<DeliveryLineListResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, deliveryOrders);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getDeliveryLine(@PathVariable Long id) {
    DeliveryLineDetailsResponse deliveryLine = deliveryLineService.findDeliveryLineById(id);
    DataResponse<DeliveryLineDetailsResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS, 
        deliveryLine);
    return ResponseEntity.status(response.status()).body(response);
  }



  // TODO: FALTA PROBAR LA SUMATORIA DE LOS TOTALES CUANDO HAYA CANTIDAD ENTREGADA EN UNA LINEA DE ENTREGA
  @PutMapping("/{id}")
  public ResponseEntity<?> updateDeliveryLine(Authentication authentication, @PathVariable Long id, @Valid @RequestBody DeliveryLineUpdateRequest deliveryLineUpdateRequest,
    BindingResult result) {
    Long id_user_authenticated = authenticationContextService.extractUserIdFromAuthentication(authentication);

    validationService.validateFieldsAndThrowResponse(result);

    deliveryLineService.updateDeliveryLineById(id, deliveryLineUpdateRequest, id_user_authenticated);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se actualizo la linea de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }

  // RECORDAR QUE SOLAMENTE PODRA BORRAR UNA LINEA DE ENTREGA SI NO HAY CANTIDAD
  // ENTREGADA
  @PatchMapping("/{id}/cancel")
  public ResponseEntity<?> cancelDeliveryLine(Authentication authentication, @PathVariable Long id) {
    Long id_user_authenticated = authenticationContextService.extractUserIdFromAuthentication(authentication);

    deliveryLineService.cancelDeliveryLineById(id, id_user_authenticated);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cancelado la linea de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }

  // ACTUALIZAR EL ESTADO DE LA LINEA DE ENTREGA SI FUE ENTREGADO
  // Solamente si tiene el estado READY
  @PatchMapping("/{id}/deliver")
  public ResponseEntity<CommonResponse> sendDeliveryLine(Authentication authentication,
      @PathVariable Long id) {

    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    deliveryLineService.sendDeliveryLineById(id, id_user);
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha entregado la linea de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }



  @PutMapping("/{id}/missing")
  public ResponseEntity<CommonResponse> lostDeliveryLine(Authentication authentication,
      @Valid @RequestBody DeliveryLineAlterRequest deliveryLineAlterRequest, BindingResult result,
      @PathVariable Long id) {
    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    deliveryLineService.lostDeliveryLineById(id, deliveryLineAlterRequest, id_user);
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha reportado la perdida de la linea de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping("/{id}/return")
  public ResponseEntity<CommonResponse> returnDeliveryLine(Authentication authentication,
      @Valid @RequestBody DeliveryLineAlterRequest deliveryLineAlterRequest, BindingResult result,
      @PathVariable Long id) {
    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    deliveryLineService.returnDeliveryLineById(id, deliveryLineAlterRequest, id_user);
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha devuelto una parte de la linea de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping("/{id}/allocate-stock")
  public ResponseEntity<CommonResponse> allocateStockInDeliveryLine(Authentication authentication,
      @Valid @RequestBody DeliveryLineAllocateRequest deliveryLineAllocateRequest, BindingResult result,
      @PathVariable Long id) {
    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    deliveryLineService.allocateDeliveryLineById(id, deliveryLineAllocateRequest, id_user);
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha asignado cantidad en toda o una parte de la linea de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }



}
