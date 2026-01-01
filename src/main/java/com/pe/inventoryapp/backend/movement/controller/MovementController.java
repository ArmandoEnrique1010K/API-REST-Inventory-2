package com.pe.inventoryapp.backend.movement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.movement.model.request.MovementAdjustmentRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementAllocateRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReturnRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReceiveRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementTransferRequest;
import com.pe.inventoryapp.backend.movement.service.MovementService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/movement")
public class MovementController {

  @Autowired
  private AuthenticationContextService authenticationContextService;
  @Autowired
  private MovementService movementService;
  @Autowired
  private ValidationService validationService;
  @Autowired
  private ResponseService responseService;

  @PostMapping("/receive")
  public ResponseEntity<CommonResponse> receiveStockLot(Authentication authentication,
      @Valid @RequestBody MovementReceiveRequest movementReceiveRequest,
      BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementService.saveMovementReceive(movementReceiveRequest, id);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha agregado contenido al stock"));
  }

  @PostMapping("/increase")
  public ResponseEntity<CommonResponse> adjustmentIncreaseStockLot(Authentication authentication,
      @Valid @RequestBody MovementAdjustmentRequest movementAdjustmentRequest,
      BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementService.saveMovementAdjustmentIncrease(movementAdjustmentRequest, id);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha aumentado manualmente el stock del producto"));
  }


  @PostMapping("/loss")
  public ResponseEntity<CommonResponse> adjustmentLossStockLot(Authentication authentication,
          @Valid @RequestBody MovementAdjustmentRequest movementAdjustmentRequest,
          BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementService.saveMovementAdjustmentLoss(movementAdjustmentRequest, id);

      return ResponseEntity.status(201)
              .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
                      "Se ha descontado manualmente el stock de un producto"));
  }

  @PostMapping("/recovery")
  public ResponseEntity<CommonResponse> adjustmentRecoveryStockLot(Authentication authentication,
          @Valid @RequestBody MovementAdjustmentRequest movementAdjustmentRequest,
          BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementService.saveMovementAdjustmentRecovery(movementAdjustmentRequest, id);

      return ResponseEntity.status(201)
              .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
                      "Se ha recuperado el stock dañado de un producto"));
  }


  @PostMapping("/transfer")
  public ResponseEntity<CommonResponse> transferStockLot(Authentication authentication,
      @Valid @RequestBody MovementTransferRequest movementTransferRequest,
      BindingResult result) {
    Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    movementService.saveMovementTransfer(movementTransferRequest, id);
      
    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha realizado una transferencia entre 2 stocks"));
  }


  @PostMapping("/allocate")
  public ResponseEntity<CommonResponse> allocateDeliveryLine(Authentication authentication,
      @Valid @RequestBody MovementAllocateRequest movementAllocateRequest,
      BindingResult result) {
    Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    movementService.saveMovementAllocate(movementAllocateRequest, id);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha preparado una linea de entrega"));
  }

  @PostMapping("/return")
  public ResponseEntity<CommonResponse> returnDeliveryLine(Authentication authentication,
          @Valid @RequestBody MovementReturnRequest movementReturnRequest,
          BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementService.saveMovementReturn(movementReturnRequest, id);

      return ResponseEntity.status(201)
              .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
                      "Se ha devuelto una linea de entrega"));
  }
  
}
