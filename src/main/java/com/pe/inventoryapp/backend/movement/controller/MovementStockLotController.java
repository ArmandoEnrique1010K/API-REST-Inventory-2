package com.pe.inventoryapp.backend.movement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.movement.model.request.MovementAdjustmentRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReceiveRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementTransferRequest;
import com.pe.inventoryapp.backend.movement.service.MovementStockLotService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movements/stock-lots")
public class MovementStockLotController {
    @Autowired
  private AuthenticationContextService authenticationContextService;
  @Autowired
  private MovementStockLotService movementStockLotService;
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
      movementStockLotService.saveMovementReceive(movementReceiveRequest, id);

      CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
              "Se agrego un nuevo lote de stock");
      return ResponseEntity.status(response.status()).body(response);
  }

  @PostMapping("/increase")
  public ResponseEntity<CommonResponse> increaseStockLot(Authentication authentication,
      @Valid @RequestBody MovementAdjustmentRequest movementAdjustmentRequest,
      BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementStockLotService.saveMovementIncrease(movementAdjustmentRequest, id);

      CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
              "Se aumento la cantidad de un lote de stock");
      return ResponseEntity.status(response.status()).body(response);
  }


  @PostMapping("/decrease")
  public ResponseEntity<CommonResponse> decreaseStockLot(Authentication authentication,
          @Valid @RequestBody MovementAdjustmentRequest movementAdjustmentRequest,
          BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementStockLotService.saveMovementDecrease(movementAdjustmentRequest, id);

      CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
              "Se disminuyo la cantidad de un lote de stock");
      return ResponseEntity.status(response.status()).body(response);
  }

  @PostMapping("/recovery")
  public ResponseEntity<CommonResponse> recoveryStockLot(Authentication authentication,
          @Valid @RequestBody MovementAdjustmentRequest movementAdjustmentRequest,
          BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementStockLotService.saveMovementRecovery(movementAdjustmentRequest, id);

      CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
              "Se recupero una cantidad dañada de un lote de stock");
      return ResponseEntity.status(response.status()).body(response);
  }


  @PostMapping("/transfer")
  public ResponseEntity<CommonResponse> transferStockLot(Authentication authentication,
      @Valid @RequestBody MovementTransferRequest movementTransferRequest,
      BindingResult result) {
    Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    movementStockLotService.saveMovementTransfer(movementTransferRequest, id);
      
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
            "Se realizo una transferencia entre 2 lotes de stock");
    return ResponseEntity.status(response.status()).body(response);
  }


}
