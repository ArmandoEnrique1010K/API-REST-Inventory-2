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
import com.pe.inventoryapp.backend.movement.model.request.MovementLossRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReturnRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementSendRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementTransferRequest;
import com.pe.inventoryapp.backend.movement.service.MovementService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;
import com.pe.inventoryapp.backend.stock.service.StockLotService;

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
  @Autowired
  private StockLotService stockLotService;

  @PostMapping("/receive")
  public ResponseEntity<CommonResponse> sendStockLot(Authentication authentication,
      @Valid @RequestBody MovementSendRequest movementSendRequest,
      BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementService.saveMovementSend(movementSendRequest, id);
      // stockLotService.sumAvailableQuantityByProductId(movementSendRequest.getIdProduct());

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha agregado contenido al stock"));
  }

  @PostMapping("/adjustment")
  public ResponseEntity<CommonResponse> adjustmentStockLot(Authentication authentication,
      @Valid @RequestBody MovementAdjustmentRequest movementAdjustmentRequest,
      BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementService.saveMovementAdjustment(movementAdjustmentRequest, id);


      // stockLotService.sumAvailableQuantityByProductId(movementAdjustmentRequest.getIdStockLot());

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha modificado manualmente el stock del producto"));
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

  @PostMapping("/loss")
  public ResponseEntity<CommonResponse> lossStockLot(Authentication authentication,
      @Valid @RequestBody MovementLossRequest movementLossRequest,
      BindingResult result) {
    Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    movementService.saveMovementLoss(movementLossRequest, id);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha descontado el stock de un producto"));
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
